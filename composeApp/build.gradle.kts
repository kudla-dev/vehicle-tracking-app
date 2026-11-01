import java.net.URI
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleKsp)
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
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {

        androidMain.dependencies {
            implementation(compose.preview)
            implementation(libs.androidx.activity.compose)

            implementation(libs.bundles.koin.android)
            implementation(libs.ktor.client.android)
            implementation(libs.androidx.work.runtime)
        }
        commonMain.dependencies {
            plugins.apply(libs.plugins.jetbrains.kotlin.serialization.get().pluginId)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.bundles.koin)
            implementation(libs.bundles.ktor)
            implementation(libs.bundles.coil)

            implementation(libs.navigation.compose)


            implementation(libs.bundles.paging)

            implementation(libs.bundles.peekaboo)

            implementation("com.valentinilk.shimmer:compose-shimmer:1.3.2")

            implementation(libs.bundles.haze)

            implementation(projects.model)
            implementation(projects.core.ui)
            implementation(projects.core.datastore)
            implementation(projects.core.designsystem)
            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.core.network)
            implementation(projects.feature.onboarding)
            implementation(projects.feature.vehicles)
            implementation(projects.feature.vehicleDetail)
            implementation(projects.feature.favourite)
            implementation(projects.feature.tracking)
            implementation(projects.feature.history)
            implementation(projects.feature.menu.main)
            implementation(projects.feature.menu.adminSettings)
            implementation(projects.feature.menu.manageVehicles)
            implementation(projects.feature.menu.manageTrackings)
            implementation(projects.feature.menu.trackingDetail)
            implementation(projects.feature.menu.protocols)
            implementation(projects.feature.search)

        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "cz.kudladev.vehicletracking"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

//    androidResources {
//        generateLocaleConfig = true
//    }
    buildFeatures { buildConfig = true }

    defaultConfig {
        applicationId = "cz.kudladev.vehicletracking"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    flavorDimensions += "environment"
    productFlavors {
        listOf("mock", "dev", "stage", "prod").forEach { environment ->
            create(environment) {
                dimension = "environment"
                // DEV retains the existing sandbox; all other installs are separate.
                if (environment != "dev") applicationIdSuffix = ".$environment"
                resValue("string", "app_name", if (environment == "prod") "Motopark" else "Motopark ${environment.uppercase()}")
                buildConfigField("String", "APP_ENVIRONMENT", "\"${environment.uppercase()}\"")
                val apiUrl = when (environment) {
                    "mock" -> ""
                    "dev" -> providers.gradleProperty("motopark.dev.apiBaseUrl")
                        .orElse(providers.gradleProperty("motopark.apiBaseUrl"))
                        .orElse("http://10.0.2.2:8080").get()
                    else -> providers.gradleProperty("motopark.$environment.apiBaseUrl").orElse("").get()
                }
                require(apiUrl.none { it.code < 32 }) { "Invalid API URL" }
                val quoted = "\"" + apiUrl.replace("\\", "\\\\").replace("\"", "\\\"") + "\""
                buildConfigField("String", "API_BASE_URL", quoted)
                if (environment in listOf("stage", "prod")) {
                    // Validate only selected variants; DEV/tests need no hosted service configuration.
                    tasks.configureEach {
                        if (name.matches(Regex("pre${environment.replaceFirstChar { it.uppercase() }}(Debug|Release)Build"))) {
                            doFirst {
                                val uri = URI(apiUrl)
                                require(uri.scheme == "https" && !uri.host.isNullOrBlank() &&
                                    uri.rawUserInfo == null && uri.rawQuery == null && uri.rawFragment == null &&
                                    uri.rawPath in listOf("", "/") && uri.port in -1..65535 && uri.port != 0) {
                                    "Supply explicit HTTPS origin with -Pmotopark.$environment.apiBaseUrl=..."
                                }
                            }
                        }
                    }
                }
            }
        }
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
