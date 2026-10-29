import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "CoreNetwork"
            isStatic = true
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
            implementation("io.ktor:ktor-client-mock:${libs.versions.ktor.get()}")
        }


        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
        }
        commonMain.dependencies {
            plugins.apply(libs.plugins.jetbrains.kotlin.serialization.get().pluginId)

            implementation(compose.runtime)
            implementation(compose.foundation)

            implementation(projects.core.domain)
            implementation(projects.model)
            implementation(projects.core.datastore)

            implementation(libs.bundles.koin)
            implementation(libs.bundles.ktor)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "cz.kudladev.vehicletracking.core.network"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    buildFeatures { buildConfig = true }

    defaultConfig {
        val apiUrl = providers.gradleProperty("motopark.apiBaseUrl").orElse("http://10.0.2.2:8080").get()
        require(apiUrl.none { it == '\n' || it == '\r' }) { "Invalid API URL" }
        val quoted = "\"" + apiUrl.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
        buildConfigField("String", "API_BASE_URL", quoted)

        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    debugImplementation(compose.uiTooling)
}
