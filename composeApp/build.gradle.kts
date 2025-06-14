import org.jetbrains.compose.desktop.application.dsl.TargetFormat
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
            implementation(libs.bundles.datastore)
            implementation(libs.bundles.coil)

            implementation(libs.navigation.compose)

            implementation(libs.compose.stacked.snackbar)

            implementation(libs.bundles.paging)

            implementation(libs.bundles.peekaboo)

            implementation(libs.flexible.bottomsheet.material3)

            implementation("com.github.GIGAMOLE:ComposeScrollbars:1.0.4")

            implementation("com.valentinilk.shimmer:compose-shimmer:1.3.2")

        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }

    project.configurations.configureEach {
        resolutionStrategy {
            force("androidx.emoji2:emoji2-views-helper:1.3.0")
            force("androidx.emoji2:emoji2:1.3.0")
        }
    }
}

android {
    namespace = "cz.kudladev.vehicletracking"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "cz.kudladev.vehicletracking"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
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
