package cz.kudladev.vehicletracking.core.data.images

import cz.kudladev.vehicletracking.core.data.images.upload.ImageUploadClient
import cz.kudladev.vehicletracking.core.data.images.upload.awaitUploadedImage
import cz.kudladev.vehicletracking.model.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertFailsWith

class ImageUploadClientTest {
    private val image = """{"id":"photo-1","url":"http://localhost/images/photo-1","position":0}"""
    private fun parent(images: String) = """{"id":"reservation-1","stateLogs":[{"state":"active","images":[$images]}],"ignored":"nested parent fields"}"""

    @Test fun trackingUploadReadsPhotoFromParentResponse() = runTest {
        val methods = mutableListOf<HttpMethod>()
        val client = client { method ->
            methods += method
            if (method == HttpMethod.Get) parent("") else parent(image)
        }
        try {
            val result = ImageUploadClient(client).tracking(byteArrayOf(1), "reservation-1", 0, "active").awaitUploadedImage()
            assertEquals("photo-1", assertIs<Result.Success<*>>(result).data.let {
                (it as cz.kudladev.vehicletracking.model.ImageWithUrl).id
            })
            assertEquals(listOf(HttpMethod.Get, HttpMethod.Post), methods)
        } finally { client.close() }
    }

    @Test fun oldPhotoCannotConfirmAnotherUpload() = runTest {
        val client = client { parent(image) }
        try {
            assertIs<Result.Error>(ImageUploadClient(client).tracking(byteArrayOf(1), "reservation-1", 0, "active").awaitUploadedImage())
        } finally { client.close() }
    }

    @Test fun wrongParentAndAmbiguousPhotosDoNotConfirmUpload() = runTest {
        for (response in listOf(parent(image).replace("reservation-1", "other"), parent(image.replace("position\":0", "position\":1")), parent("$image,${image.replace("photo-1", "photo-2")}"))) {
            val client = client { if (it == HttpMethod.Get) parent("") else response }
            try {
                assertIs<Result.Error>(ImageUploadClient(client).tracking(byteArrayOf(1), "reservation-1", 0, "active").awaitUploadedImage())
            } finally { client.close() }
        }
    }

    @Test fun vehicleHttpFailureIsEmittedEvenAfterProgress() = runTest {
        val client = HttpClient(MockEngine { request ->
            if (request.method == HttpMethod.Get) respond("""{"id":7,"images":[]}""", headers = headersOf("Content-Type", "application/json"))
            else respond("unavailable", HttpStatusCode.ServiceUnavailable)
        }) { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
        try {
            val result = ImageUploadClient(client).vehicle(byteArrayOf(1), 7, 0).awaitUploadedImage()
            assertEquals(503, assertIs<Result.Error>(result).error.status)
        } finally { client.close() }
    }

    @Test fun invalidJsonIsAnErrorRatherThanSuccessOrUncaughtDecoderException() = runTest {
        val client = client { if (it == HttpMethod.Get) parent("") else "not JSON" }
        try {
            assertIs<Result.Error>(ImageUploadClient(client).tracking(byteArrayOf(1), "reservation-1", 0, "active").awaitUploadedImage())
        } finally { client.close() }
    }

    @Test fun cancellationPropagates() = runTest {
        val client = HttpClient(MockEngine { throw CancellationException("test cancellation") })
        try {
            assertFailsWith<CancellationException> {
                ImageUploadClient(client).vehicle(byteArrayOf(1), 7, 0).awaitUploadedImage()
            }
        } finally { client.close() }
    }

    @Test fun identicalLegacyReferencesAreAcceptedOnlyOnce() = runTest {
        val client = client { if (it == HttpMethod.Get) parent("") else parent("$image,$image") }
        try {
            assertIs<Result.Success<*>>(ImageUploadClient(client).tracking(byteArrayOf(1), "reservation-1", 0, "active").awaitUploadedImage())
        } finally { client.close() }
    }

    @Test fun vehicleUrlImportAlsoRequiresANewImage() = runTest {
        val client = client { method ->
            if (method == HttpMethod.Get) """{"id":7,"images":[]}"""
            else """{"id":7,"images":[$image]}"""
        }
        try {
            val result = ImageUploadClient(client).vehicleUrl("https://example.invalid/photo.jpg", 7, 0).awaitUploadedImage()
            assertIs<Result.Success<*>>(result)
        } finally { client.close() }
    }

    @Test fun deniedPreflightNeverPostsThePhoto() = runTest {
        val methods = mutableListOf<HttpMethod>()
        val client = HttpClient(MockEngine { request ->
            methods += request.method
            respond("denied", HttpStatusCode.Forbidden)
        })
        try {
            val result = ImageUploadClient(client).vehicle(byteArrayOf(1), 7, 0).awaitUploadedImage()
            assertEquals(403, assertIs<Result.Error>(result).error.status)
            assertEquals(listOf(HttpMethod.Get), methods)
        } finally { client.close() }
    }

    @Test fun reconciliationUsesOnlyGetAndRequiresUniqueExactBytes() = runTest {
        for (count in 0..2) {
            val methods = mutableListOf<HttpMethod>()
            val http = HttpClient(MockEngine { request ->
                methods += request.method
                if (request.url.encodedPath == "/vehicles/7") {
                    val images = (1..count).joinToString(",") { n ->
                        """{"id":"p$n","url":"http://localhost/images/p$n","position":0}"""
                    }
                    respond("""{"id":7,"images":[$images]}""", headers = headersOf("Content-Type", "application/json"))
                } else respond(byteArrayOf(1, 2))
            }) { install(ContentNegotiation) { json() } }
            try {
                val record = cz.kudladev.vehicletracking.core.data.images.upload.UploadRecord("job", "tag", vehicleId = 7, position = 0, previousImageIds = emptySet())
                val match = ImageUploadClient(http).reconcile(record, byteArrayOf(1, 2))
                assertEquals(count == 1, match != null)
                assertEquals(null, ImageUploadClient(http).reconcile(record.copy(previousImageIds = null), byteArrayOf(1, 2)))
                assertEquals(null, ImageUploadClient(http).reconcile(record.copy(previousImageIds = setOf("p1", "p2")), byteArrayOf(1, 2)))
                assertEquals(null, ImageUploadClient(http).reconcile(record, byteArrayOf(9)))
                kotlin.test.assertTrue(methods.all { it == HttpMethod.Get })
            } finally { http.close() }
        }
    }

    @Test fun newPhotoWithWrongBytesCannotConfirmPost() = runTest {
        val http = client { if (it == HttpMethod.Get) parent("") else parent(image) }
        try {
            assertIs<Result.Error>(ImageUploadClient(http).tracking(byteArrayOf(9), "reservation-1", 0, "active").awaitUploadedImage())
        } finally { http.close() }
    }

    @Test fun conflictingReferencesToSameIdCannotConfirmRecovery() = runTest {
        val http = client { """{"id":7,"images":[$image,${image.replace("position\":0", "position\":1")}]}""" }
        try {
            val record = cz.kudladev.vehicletracking.core.data.images.upload.UploadRecord(
                "job", "tag", vehicleId = 7, position = 0, previousImageIds = emptySet())
            assertEquals(null, ImageUploadClient(http).reconcile(record, byteArrayOf(1)))
        } finally { http.close() }
    }

    private fun client(body: (HttpMethod) -> String) = HttpClient(MockEngine { request ->
        if (request.url.encodedPath.startsWith("/images/")) respond(byteArrayOf(1))
        else respond(body(request.method), headers = headersOf("Content-Type", "application/json"))
    }) { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
}
