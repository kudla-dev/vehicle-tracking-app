# Motopark client

Kotlin Multiplatform / Compose client for Android and iOS. Reservations and
pickup/return photo protocols use the separate Spring backend repository.

## Android build

Use JDK 17, the checked-in Gradle wrapper, Android SDK platform 35 and Build Tools
35.0.0. The Android Java/Kotlin output target remains JVM 11.

```sh
# Homebrew paths on this development machine; adjust for your installation.
export JAVA_HOME=/opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk/Contents/Home
export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
bash ./gradlew :composeApp:assembleDebug :core:data:testDebugUnitTest :core:network:testDebugUnitTest :composeApp:lintDebug
```

On a new machine, install the SDK packages after accepting Google's SDK licenses.
You can also point ignored `local.properties` at an existing SDK with `sdk.dir`.
Do not commit machine-specific absolute SDK or JDK paths.

The debug APK is generated at `composeApp/build/outputs/apk/debug/composeApp-debug.apk`.
Lint reports are in `composeApp/build/reports/`. A unit-test task reporting
`NO-SOURCE` means no tests ran, not that behavior was tested.

Device installation is separate from building:

```sh
adb devices -l
bash ./gradlew :composeApp:installDebug
adb shell am start -n cz.kudladev.vehicletracking/.MainActivity
```

## API configuration

Android defaults to `http://10.0.2.2:8080`, the host address seen by the Android
emulator. Override it at build time for a device or another isolated backend:

```sh
bash ./gradlew :composeApp:assembleDebug -Pmotopark.apiBaseUrl=http://192.168.1.20:8080
```

`composeApp` generates its `BuildConfig.API_BASE_URL` from this property and passes
an immutable `AppConfig` to Koin. `core/network` receives the configuration and
does not read platform settings itself. The
debug variant permits HTTP to development hosts. Other variants retain the main
network security configuration; configure the intended HTTPS origin before a
hosted release. The defaults target local development. `NetworkConstants` is legacy code
and is not the active configuration source.

iOS reads `MOTOPARK_API_BASE_URL` from the process environment first (for example
an Xcode scheme variable). Otherwise it reads the `MotoparkAPIBaseURL` Info.plist
entry, assembled from `MOTOPARK_API_SCHEME`, `MOTOPARK_API_HOST` and
`MOTOPARK_API_PORT` in `Configuration/Config.xcconfig`. Defaults are
`http`, `127.0.0.1`, `8080` for the simulator. Set a reachable host for a physical
device. Process-variable changes apply on the next application launch.

Only an absolute HTTP(S) origin is accepted: no `/api` prefix, query, fragment,
credentials or wildcard server address. Existing repository routes start at `/`.
The HTTP client withholds API bearer tokens from foreign image hosts, including
401 challenges and cross-origin redirects. Session persistence/refresh hardening
remains separate work.

Use an isolated backend when verifying the app. A successful build does not
establish device connectivity or complete API compatibility.

### Environment configuration foundation

The shared `AppConfig` models `MOCK`, `DEV`, `STAGE` and `PROD`. Every network
environment requires an explicit, validated API origin. Stage and production
require HTTPS, including image requests and redirects through the shared HTTP
client. Mock has no remote API origin and cannot initialize the native network
module; its dedicated transport is a separate upcoming step.

Both platform hosts currently select **DEV** explicitly and preserve the existing
API overrides and local defaults described above. No environment switch is exposed
yet: Android flavors, iOS schemes, application IDs and storage isolation must be
implemented together before enabling stage/prod/mock builds. A release build is
therefore not yet a production environment. Do not reuse the current app's stored
session or pending uploads by switching its origin to another environment.

`AppConfigTest` covers required HTTPS, invalid origins, mock network rejection,
DI configuration identity and blocking HTTP image requests/redirects before send.

## iOS

The project targets iOS device ARM64 and simulator ARM64/x64. Full Xcode is
required to link the framework and build/run the SwiftUI host. Shared Kotlin
compilation for `iosSimulatorArm64` passes; full linking currently stops at the
missing Xcode installation.

iOS image uploads persist photos and a job journal in Application Support before
sending, expose progress and protocol tags, and keep evidence on failures or
cancellation. Interrupted uploads are restored as failures requiring review;
they are not automatically posted again because the API is not idempotent.
Transfers currently run only while the application is executing. A shared recovery UI provides local previews, GET-only byte reconciliation and
explicit manual resubmission after reviewing the server. Native background
URLSession execution and device verification remain open work.

Recovery behavior, regression checks and native runtime limitations are documented in
[docs/upload-recovery.md](docs/upload-recovery.md).

The common upload tests run with `:core:data:testDebugUnitTest`. They verify
acknowledgements, error/cancellation handling and evidence preservation. Native
filesystem/permissions/session startup still require a simulator or device.

## Source layout

* `/composeApp` is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - `commonMain` is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    `iosMain` would be the right folder for such calls.

* `/iosApp` contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform, 
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.


Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…
