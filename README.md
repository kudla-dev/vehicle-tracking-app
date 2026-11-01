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
bash ./gradlew :composeApp:assembleDevDebug :core:datastore:testDebugUnitTest :core:data:testDebugUnitTest :core:network:testDebugUnitTest :composeApp:lintDevDebug
```

On a new machine, install the SDK packages after accepting Google's SDK licenses.
You can also point ignored `local.properties` at an existing SDK with `sdk.dir`.
Do not commit machine-specific absolute SDK or JDK paths.

The debug APK is generated at `composeApp/build/outputs/apk/dev/debug/composeApp-dev-debug.apk`.
Lint reports are in `composeApp/build/reports/`. A unit-test task reporting
`NO-SOURCE` means no tests ran, not that behavior was tested.

Device installation is separate from building:

```sh
adb devices -l
bash ./gradlew :composeApp:installDevDebug
adb shell am start -n cz.kudladev.vehicletracking/.MainActivity
```

## API configuration

Android defaults to `http://10.0.2.2:8080`, the host address seen by the Android
emulator. Override it at build time for a device or another isolated backend:

```sh
bash ./gradlew :composeApp:assembleDevDebug -Pmotopark.apiBaseUrl=http://192.168.1.20:8080
```

`composeApp` owns the `mock`, `dev`, `stage`, `prod` flavors. Feature modules
remain flavor-free. DEV retains the previous application ID and development
sandbox. Other environments have distinct IDs and visible names. A release build
uses the environment of its flavor: use `prodRelease` for production packaging.

Stage/prod have **no default API**. Supply explicit HTTPS origins with
`-Pmotopark.stage.apiBaseUrl=...` / `-Pmotopark.prod.apiBaseUrl=...`.
`motopark.apiBaseUrl` remains a DEV-only compatibility alias;
`motopark.dev.apiBaseUrl` takes precedence. Only DEV permits native cleartext.
Mock starts an unavailable-transport notice, never application/network services;
it is not yet a functional mock client. Android mock also has no INTERNET permission.

In Xcode choose `Motopark-Dev`, `Motopark-Mock`, `Motopark-Stage` or `Motopark-Prod`.
Each shared scheme uses separate Debug/Release configurations and xcconfig files.
DEV keeps the old bundle ID. Set API SCHEME/HOST/PORT in an ignored
`iosApp/Configuration/Stage.local.xcconfig` or `Prod.local.xcconfig`; both require
explicit `https`, host and port. DEV alone defaults to `http`, `127.0.0.1`, `8080`.
A process `MOTOPARK_API_BASE_URL` overrides the bundled origin at launch and passes
the same validation and storage isolation. It cannot change the build environment.

Session/user preferences and upload journals/photos are isolated by environment
and a SHA-256 of the canonical API origin (scheme, host, effective port). Returning
to a previous origin restores only its own state. **Legacy unscoped data is retained
but not adopted**, because its server cannot be established. Existing installations
will need to sign in again. See [environment configuration and recovery details](docs/environment-config.md)
for paths, test commands and native verification limits. Keychain is not used in
this source tree; tokens currently remain in sandbox DataStore, not encrypted storage.

Only an absolute HTTP(S) origin is accepted: no path prefix, query, fragment,
credentials or wildcard address. API bearer tokens are withheld from foreign image
hosts and cross-origin redirects. No real stage/prod service is configured by this change.

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
