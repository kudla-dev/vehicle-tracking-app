# Environment variants and isolation — 2026-11-01

The application host selects the environment at build time and supplies `AppConfig`
to Koin. Only `composeApp` has Android flavors; libraries/features still publish
ordinary Debug/Release variants. Mock transport is **not implemented**: both hosts
show a notice without initializing Koin, application services or the network engine.
Android mock removes INTERNET permission as an additional native barrier.

## Identities and configuration

| Environment | Android application ID | iOS bundle ID | Visible name |
| --- | --- | --- | --- |
| dev | `cz.kudladev.vehicletracking` | `cz.kudladev.vehicletracking.VehicleTracking` | Motopark DEV |
| mock | `cz.kudladev.vehicletracking.mock` | `cz.kudladev.vehicletracking.VehicleTracking.mock` | Motopark MOCK |
| stage | `cz.kudladev.vehicletracking.stage` | `cz.kudladev.vehicletracking.VehicleTracking.stage` | Motopark STAGE |
| prod | `cz.kudladev.vehicletracking.prod` | `cz.kudladev.vehicletracking.VehicleTracking.prod` | Motopark |

The previous DEV IDs remain unchanged, preserving installed development sandboxes.
Android build types are independent of environments (`devRelease` is still DEV).
Distinct IDs allow parallel installs in principle; device installation is not verified.
Android backups are disabled for new builds to prevent restoring session/upload state
into another installation. Existing files are not deleted by this manifest change.

Android API properties:

- DEV: `motopark.dev.apiBaseUrl`, then legacy `motopark.apiBaseUrl`, then emulator
  default `http://10.0.2.2:8080`.
- STAGE/PROD: `motopark.stage.apiBaseUrl` / `motopark.prod.apiBaseUrl`; required,
  HTTPS only. Selected builds fail without a value. The legacy property cannot
  configure either environment.
- MOCK: no API address; API properties do not enable a real transport.

Only DEV has a native cleartext exception (for both debug/release). Stage/prod
also retain the common HTTPS request/redirect guard. No service addresses or
infrastructure are provisioned. Reserved `.invalid` addresses below are packaging
fixtures, not services to run the app against.

Xcode shared schemes `Motopark-Dev/Mock/Stage/Prod` select matching environment
Debug and Release configurations. `KOTLIN_FRAMEWORK_BUILD_TYPE` maps these custom
configuration names to Kotlin Debug/Release. Legacy Xcode Debug/Release aliases
remain DEV. Each environment has `iosApp/Configuration/<Environment>.xcconfig`.
Only DEV defaults to HTTP/127.0.0.1/8080. Supply HTTPS SCHEME, HOST and PORT in
ignored `Stage.local.xcconfig` / `Prod.local.xcconfig` or Xcode build settings:

```xcconfig
MOTOPARK_API_SCHEME=https
MOTOPARK_API_HOST=<explicit host supplied by your environment>
MOTOPARK_API_PORT=443
```

A hosted Xcode build fails if these values are missing or use HTTP. The host
validates the final origin again on launch. `MOTOPARK_API_BASE_URL` in the process
environment overrides the bundled origin, but never the build environment or
HTTPS requirement. Only the DEV plist enables arbitrary/local networking; hosted
and mock plists have default ATS without exceptions. Runtime TLS/connectivity is
still unverified.

## Persistence and existing development evidence

The namespace is `environments/v1-<environment>-<SHA-256 of canonical origin>`.
The canonical origin includes lowercased scheme/host and effective port, with no
path. Equivalent casing, trailing `/` and default-port spelling reuse the namespace.
Changing scheme, host, port or environment selects separate data. Switching back
to an old origin restores that origin's own session and pending evidence.

Both session/user DataStore and upload journals/bytes use this same namespace:

- Android: `files/<namespace>/vehicle_tracking.preferences_pb` and
  `files/<namespace>/image-uploads/`.
- iOS: `Library/Application Support/<namespace>/vehicle_tracking.preferences_pb`
  and `<namespace>/image-uploads/`.

The DataStore cache is keyed by full path instead of a process-wide single store.
Android WorkManager persists the namespace in job input and unique-work names;
a worker whose namespace is missing or different is a no-op before reading or
posting current records. Legacy workers also remain fail-closed. iOS queues use
only the selected scoped directory and never scan other scopes. Interrupted work
still requires review; no automatic POST retry was introduced. Coil disk cache
is already disabled; its memory cache disappears with the process. Changing an
origin requires a rebuilt Android app or a new iOS launch; live switching is not exposed.

**Unscoped legacy tokens, user data, upload journals and photographs remain in
place, but are not imported automatically.** Their server cannot be determined,
so adopting them for the first new origin would risk cross-server submission.
The first scoped launch requires sign-in. This applies even to the default local
DEV origin. Development backend data is unchanged.

For manual evidence recovery, retain/export these old locations from the DEV
sandbox before uninstalling or clearing app data:

- Android: `files/vehicle_tracking.preferences_pb`, `files/image-uploads/`,
  `files/temp_image_*.jpg`.
- iOS: `Library/Application Support/vehicle_tracking.preferences_pb`, the former
  `Library/Documentation/vehicle_tracking.preferences_pb` if present, and
  `Library/Application Support/image-uploads/`.

Do not copy old session/preferences or journals to a new namespace. After verifying
server/owner/target and whether an upload already succeeded, an operator can export
photos and attach them through the current workflow. There is no legacy import UI
or automatic migration. Old photographs are not deleted; normal scoped success
cleanup only deletes bytes from its own namespace.

### Keychain review

The current source tree does **not** use Security/Keychain, access groups or shared
Keychain entitlements. Both platforms currently persist tokens in sandbox DataStore.
The previous roadmap wording claiming secure platform storage was too strong.
This change isolates that store; it does not implement encryption or Keychain.
Secure token storage remains open before first deployment. Any future Keychain
service/account must include bundle identity **and** this origin namespace, must
avoid shared access groups by default, and must not adopt unbound legacy credentials.
Keychain reinstall persistence is therefore not part of the current implementation.

## Reproduction

Use the repository wrapper, JDK 17 and SDK 35. For this machine see README for
JAVA_HOME/ANDROID_HOME. A 2 GB Gradle heap ran out of memory in D8 when building
multiple fresh flavors; the following command uses 4 GB and two workers:

```sh
./gradlew :core:datastore:testDebugUnitTest :core:network:testDebugUnitTest \
  :core:data:testDebugUnitTest :feature:vehicle_detail:testDebugUnitTest \
  :composeApp:assembleDevDebug :composeApp:assembleMockDebug \
  :composeApp:assembleStageDebug :composeApp:assembleProdRelease \
  :composeApp:lintDevDebug :composeApp:lintMockDebug \
  :composeApp:lintStageDebug :composeApp:lintProdRelease \
  compileKotlinIosSimulatorArm64 \
  -Pmotopark.stage.apiBaseUrl=https://stage.example.invalid \
  -Pmotopark.prod.apiBaseUrl=https://prod.example.invalid \
  --max-workers=2 '-Dorg.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8'
python3 scripts/check-android-environments.py
python3 scripts/check-ios-environments.py
```

The same tasks are used by CI. The iOS graph check requires macOS `plutil`, not
Xcode; it validates all four shared schemes, eight paired configurations, bundle
IDs, ATS selection and seven fail-closed build guard cases. Unit regressions cover
origin namespace separation/canonicalization, foreign and legacy worker rejection,
DI passing the exact namespace, and actual preference-file/token/user separation
with legacy bytes preserved. Existing queue/transport regressions remain in place.

## Verified results and limits

Final command: **BUILD SUCCESSFUL**, 1,900 actionable tasks (94 executed, 1,806
up-to-date), 29 seconds on the incremental final run. The preceding full variant
verification succeeded in 2m35s with 884 executed tasks. The first 2 GB attempt
failed in D8 with Java heap exhaustion; the 4 GB command above resolves that failure.

- 74 selected tests in result reports, no failures/errors/skips: 1 datastore,
  23 network, 47 data, 3 vehicle detail. New namespace/preference tests ran in the
  initial verification; the final run reran the 47 data tests after the final worker
  guard change, with other suites UP-TO-DATE. The 6 separate domain tests were not rerun.
- APKs built: devDebug, mockDebug, stageDebug, prodRelease (unsigned). Lint has
  0 errors for all four; warnings respectively 55, 52, 54, 38. Warnings are not claimed fixed.
- `check-android-environments.py` passed against all four packaged APKs: distinct
  IDs/labels, no mock INTERNET permission, backups disabled, cleartext only in DEV.
- Stage missing-origin and production HTTP pre-build checks both failed as intended
  with explicit configuration errors. Hosted `.invalid` fixtures built successfully
  without contacting a server.
- All iOS simulator ARM64 Kotlin modules compiled. Existing KLIB resolver and
  expect/actual warnings remain. The iOS graph and seven build-guard cases passed.
- `adb devices -l` lists no attached device; no emulator executable/AVD was found
  in the installed SDK. `xcode-select -p` points to CommandLineTools and `xcrun
  simctl` is unavailable. No device/simulator install, parallel launch, native
  filesystem/worker/restart, iOS framework link or Swift host run was verified.
  Keychain is absent; native secure-storage/reinstall behavior is not certified.
- CI definitions are updated; hosted CI was not observed. Backend remains unchanged;
  no deployment or shared database operation was performed.

Roadmap and raw verification logs are in the workspace outside both Git repositories:
`docs/verification/2026-11-01/environment-variants-*.log`. This in-repository report
contains the reproducible commands and results even though those workspace files
are not part of the client commit.

---

The original shared-configuration foundation results are retained below as history.

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
