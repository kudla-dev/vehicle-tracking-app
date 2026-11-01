import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
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
            baseName = "CoreMock"
            isStatic = true
        }
    }

    sourceSets {
        commonTest.dependencies {
            implementation(projects.core.data)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
            implementation("io.ktor:ktor-client-mock:${libs.versions.ktor.get()}")
        }


        commonMain.dependencies {
            plugins.apply(libs.plugins.jetbrains.kotlin.serialization.get().pluginId)


            implementation(libs.kotlinx.datetime)
            implementation(projects.core.network)
            implementation("io.ktor:ktor-client-mock:${libs.versions.ktor.get()}")
            implementation(projects.core.domain)
            implementation(projects.model)
            implementation(projects.core.datastore)

            implementation(libs.bundles.koin)
            implementation(libs.bundles.ktor)
        }
    }
}

android {
    namespace = "cz.kudladev.vehicletracking.core.mock"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
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

