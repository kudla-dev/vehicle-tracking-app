package cz.kudladev.vehicletracking

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.memory.MemoryCache
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.util.DebugLogger
import cz.kudladev.vehicletracking.app.navigation.core.VehicleTracking
import cz.kudladev.vehicletracking.app.rememberAppState
import cz.kudladev.vehicletracking.core.designsystem.theme.AppTheme
import io.ktor.client.*
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
@Preview
fun App() {
    val httpClient = koinInject<HttpClient>()
    setSingletonImageLoaderFactory { context ->
        getAsyncImageLoader(context,httpClient)
    }

    val appState = rememberAppState()

    AppTheme {
        VehicleTracking(appState)
    }
}


fun getAsyncImageLoader(
    context: PlatformContext,
    httpClient: HttpClient,
) =
    ImageLoader.Builder(context)
        .diskCachePolicy(CachePolicy.DISABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .memoryCache {
            MemoryCache.Builder()
                .maxSizePercent(context, 0.1)
                .strongReferencesEnabled(true)
                .build()
        }
        .components {
            add(
                KtorNetworkFetcherFactory(
                    httpClient = httpClient,
                )
            )
        }
        .crossfade(300)
        .logger(DebugLogger())
        .build()