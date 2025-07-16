rootProject.name = "VehicleTracking"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

include(":composeApp")
include(":core:ui")
include(":core:data")
include(":core:datastore")
include(":core:domain")
include(":core:network")
include(":core:designsystem")
include(":feature:onboarding")
include(":feature:favourite")
include(":feature:history")
include(":feature:vehicles")
include(":feature:vehicle_detail")
include(":feature:tracking")
include(":feature:search")
include(":feature:menu:main")
include(":feature:menu:admin_settings")
include(":feature:menu:manage_vehicles")
include(":feature:menu:manage_trackings")
include(":feature:menu:tracking_detail")
include(":feature:menu:protocols")
include(":model")
