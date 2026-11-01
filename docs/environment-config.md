# Environment configuration foundation — 2026-11-01

This completes the shared configuration foundation, not the four runnable variants.

## Changes

- Shared immutable `AppConfig` models mock/dev/stage/prod; network environments
  require an explicit origin. Stage/prod require HTTPS.
- The shared HTTP client rejects HTTP requests and HTTPS-to-HTTP redirects for
  stage/prod before they reach the engine, including external image requests.
- Mock has no API endpoint. Passing it to the native network module fails before
  DI or an engine starts; the dedicated mock transport remains to be implemented.
- Android API BuildConfig generation moved from `core/network` to `composeApp`.
  The existing `motopark.apiBaseUrl` property and emulator default are preserved.
- iOS process/Info.plist lookup moved into the application host, preserving its
  precedence and simulator default. Both hosts explicitly select DEV for now.
- The application supplies configuration to Koin. Platform network modules now
  supply only the engine; features do not need flavors or configuration lookups.
- Origin validation additionally rejects whitespace, control characters and
  backslashes instead of allowing URL parser normalization.

## Verification

JDK 17.0.21, Android SDK 35, repository wrapper:

```sh
./gradlew :core:network:testDebugUnitTest :core:data:testDebugUnitTest \
  :feature:vehicle_detail:testDebugUnitTest :composeApp:assembleDebug \
  :composeApp:lintDebug compileKotlinIosSimulatorArm64
```

Result: BUILD SUCCESSFUL. Network: 20 tests (7 new configuration tests); data:
47 tests, no failures or skipped tests. Vehicle detail: 3 tests, task UP-TO-DATE.
The selected suites total 70 tests; the separate 6 domain tests were not rerun.
Android APK built, lint: 0 errors / 54 warnings. All iOS simulator ARM64 Kotlin
modules compiled. Existing KLIB resolver and expect/actual warnings remain.

New tests cover explicit HTTPS origins, dev HTTP compatibility, invalid origins,
mock rejecting a native transport, DI preserving the supplied configuration,
HTTP images and downgrade redirects never reaching the engine. Existing token
origin/refresh and upload/contract tests remain successful.

## Next step and limits

Implement Android flavors and iOS schemes together with distinct application IDs
and storage/session/upload isolation. Until that is done, no environment selector
is exposed, and even the existing release build is DEV, not a production variant.
Changing an origin in the current app does not isolate persisted state.

The mock backend, deterministic scenarios, backend environment profiles, seeds,
Maestro flows and production native transport configuration are still pending.
HTTPS enforcement here applies to the shared Ktor client; it does not certify
every native network path or the final platform security configuration.
No native runtime, device install, iOS framework link/start or hosted CI run was
verified by these checks. No backend contract, database schema or data was changed.
