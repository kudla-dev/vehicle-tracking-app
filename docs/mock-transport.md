# Stateful offline MOCK transport — 2026-11-01

MOCK runs the ordinary application: Koin, repositories, serializers, validators,
ViewModels, Coil and upload queue. Only the HTTP engine is replaced. `core/mock`
contains an in-memory Ktor MockEngine server, never a native engine or URL fetcher.
Feature modules have no flavors or scenario branches.

## Launch and walkthrough

Build `:composeApp:assembleMockDebug` and install the mock APK. Xcode uses the
`Motopark-Mock` scheme; its Gradle phase passes `-Pmotopark.mock=true` only for
`MOTOPARK_ENVIRONMENT=MOCK`. Direct Kotlin compilation of this host wiring uses
`:composeApp:compileKotlinIosSimulatorArm64 -Pmotopark.mock=true`. Ordinary iOS
compilation excludes the mock host source directory and mock dependency.

Accounts are fixtures, not service credentials:

- `user@example.invalid`, password `Fixture-only-1!`
- `admin@example.invalid`, same password

The clean initial state has one vehicle, a local catalog image, two accounts and
no reservations. Sign in as user, browse/search/open the fixture vehicle, inspect
the calendar and reserve it. Sign out **without terminating the process**, sign in
as admin, inspect pending reservations and approve or reject. For an approved
reservation record pickup mileage, attach protocol photos and record return mileage
at least as high as pickup. Sign out/in as user to confirm completion and inspect
history. Existing UI and validation remain in use; this report does not claim that
this sequence has been driven on a device.

Vehicle ID is `2147483648`, brand ID `2147483649`, user UUID ends `001`, admin `004`.
Reservation IDs start at UUID suffix `000000000100`; upload images at `000000000200`.
Separate token counters keep entity IDs independent of sign-in/refresh counts.
Each reset repeats these sequences. Initial catalog image ends `003`. Responses
use the existing wire casing and nested DTO structure. Sanitized snapshots of the
client and backend `contract/api.json` are checked in under mock test resources;
tests decode both with actual client DTOs. The mock seed adapts those fixture
fields to two roles, a local image and a clean reservation lifecycle.

## Scenarios and controlled time

Android selects a scenario at build time with `-Pmotopark.mockScenario=NAME`.
Only the mock flavor generates `MOCK_SCENARIO`. iOS mock accepts process variable
`MOTOPARK_MOCK_SCENARIO=NAME`; other iOS hosts do not compile that lookup.
The default is `NORMAL`:

| Scenario | Behavior |
| --- | --- |
| NORMAL | Clean mutable catalog/reservation server |
| EMPTY | List responses are empty; current reservation is initially 204 |
| BAD_AUTH | Login returns 401 even with fixture credentials |
| SERVER_ERROR | Every local request returns structured 500 |
| TIMEOUT | Socket timeout before any mutation; no wall-clock sleep |
| LOST_UPLOAD_RESPONSE | First accepted binary upload commits, then loses its response; subsequent GET/replay works |

Tests can construct independent `MockServer` instances, call suspend `reset` and
`advanceTo`. Reset clears tokens/refresh tokens, reservations, uploaded image bytes
and idempotency receipts, and restores fixture entities and counters. Old tokens
remain invalid even after repeating login counters. No HTTP reset/scenario endpoint
exists. Application reset is deliberately a **new process launch**, not an in-app
button that could race a running upload or retain ViewModel/Coil/session caches.

Time starts at `2026-11-02T08:00:00Z`, stays fixed, and is shared by mock log
assignment, return time, calendar and the UI calendar/countdown through `AppClock`.
Non-mock hosts retain `Clock.System`. UI tests should also fix device timezone and
locale; the existing client converts reservation wall times using the device zone.
API calendar queries honor the requested timezone. Advancing time is monotonic;
reset can restore the initial instant. Dates absent from the calendar map mean
standard available working slots, matching the API convention.

## State, permissions and uploads

Every endpoint reads the same server state under a mutex. List/detail/current,
latest-state admin counters, calendar availability, user history, log photographs
and owner mileage totals therefore observe the same writes. Hydration resolves
the current vehicle/user rather than stale nested snapshots. Completion increments
the owner's `overallDistance` once. Vehicle `totalDistance` remains unchanged,
matching the current backend service; the mock does not silently add a server policy.

Transitions mirror current `AccessPolicy`/`TrackingService`: admins approve,
reject, fail, pick up and return; only the owner confirms completion. Illegal
transitions conflict. Pending/approved/active/returned reservations block overlap;
rejected/failed reservations release slots. Completed reservations retain historical
occupancy, matching current backend queries. Pickup/return require nonnegative mileage;
return cannot precede pickup. Normal users see only their reservations; counters,
history by user and binary uploads require admin. Protocol images require parent
access. Mock policy tests do not certify the backend implementation.

Binary vehicle and approved/active/returned protocol uploads preserve the exact
uploaded bytes and position. `Idempotency-Key` records target, position and exact
bytes. Exact replay returns the same image ID in `X-Upload-Image-Id` and the current
parent; reuse with a different payload/target/position returns 409. Parent GET and
image GET expose the committed upload even when its response was lost, allowing
the ordinary queue's GET-only reconciliation to operate. The ordinary transport
still does not automatically repeat uncertain POSTs. Android MOCK jobs do not
require connectivity; other environments retain the CONNECTED constraint.

The initial PNG is embedded existing project launcher artwork, not an external
photo. Uploaded bytes are local memory; no image URL is downloaded by the server.
URL import/fetch, scraping, registration, vehicle editing and other unimplemented
routes return clear errors (normally 501), never successful placeholder writes.
Unknown entity IDs return 404. Unsupported import URLs are never dereferenced.

## Restart, reset and evidence

Server state lasts for one process. Before starting DI on **each fresh MOCK process**,
the host moves only that MOCK namespace directory to a sibling `mock-archive-<UUID>`.
This atomically retires its DataStore session/user and entire upload journal/bytes
before any queue can restore them. Fresh fixtures start with no session or jobs.
Archives preserve unconfirmed photographs and metadata for inspection; they are
never automatically loaded/replayed. They consume sandbox storage until explicitly
removed by a developer. Uninstall/clear-data removes the platform sandbox normally.

Android WorkManager's old job entries can remain, but the current journal contains
none of their IDs, so they cannot replay retired bytes. The same namespace worker
guards still reject foreign/legacy work. iOS foreground tasks disappear with the
process. A JVM filesystem test verifies atomic archival, repeat reset, retained
session/journal/photo bytes and untouched DEV data. Native scheduling, filesystem
and process termination behavior still require device verification. Do not invoke
host initialization repeatedly in the same process as an application reset.

## Network and artifact boundary

All mock API/image requests use the injected client/engine. The server checks the
exact `https://mock.motopark.invalid:443` origin; foreign HTTPS origins/ports return
400 and HTTP is rejected by the shared HTTPS guard. There is no redirect or native
engine fallback. Coil uses that same client. Android additionally removes INTERNET.
URL import/scraping fail locally. Tests exercise foreign API/image origins, HTTP,
foreign ports and URL import endpoints without constructing any real engine.

Android depends on `core/mock` only through `mockImplementation`; other flavor
host functions use the ordinary `networkModule`. iOS selects disjoint mock/network
host source directories and only adds the mock dependency for the mock framework.
Scenario lookup and sandbox reset implementations live in those mock inputs.
`scripts/check-mock-packaging.py` scans all four APK DEX files for mock server,
engine, scenario and reset markers, requiring them present only in MOCK. iOS Kotlin
compilation and dependency graph checks cover wiring; linked iOS artifact inspection
is still blocked by the missing full Xcode installation.

## Verification and remaining limits

The verification results are recorded below. Run the existing environment scripts,
all targeted regression tests, four Android variants with lint, ordinary iOS Kotlin
compilation and the mock iOS host compilation. CI includes mock tests and packaging
checks. No shared database or infrastructure is used by this work.

No device/simulator runtime, parallel installation, native camera/permissions,
filesystem/worker restart, Swift host or framework linking is certified. Maestro,
Keychain/Keystore, native background transfer, backend deployment/policies and
infrastructure remain separate steps. Tokens are still in sandbox DataStore.

### Verified results

Final verification after the session cache fix (`3ca333e`): **BUILD SUCCESSFUL**,
1,951 tasks (107 executed, 1,844 up-to-date), 29 seconds incremental. An earlier
complete verification succeeded with 1,945 tasks in 2m54s before that fix; it is
not substituted for the final run. Separate mock iOS host compilation succeeded
with 127 tasks (2 executed), 3 seconds. All native Kotlin modules compiled.

- 97 tests in selected reports, 0 failures/errors/skips: 16 mock, 24 network,
  47 data, 6 domain, 1 datastore and 3 vehicle detail. Mock/network tests ran after
  the final session fix; the final combined command reused those passing outputs.
- Four APKs built: mockDebug/devDebug/stageDebug/prodRelease (production unsigned).
  All four lint reports have 0 errors, respectively 52/55/54/38 warnings.
- Packaged APK checks passed: separate identities, no mock INTERNET, backups off,
  DEV-only cleartext; mock server/engine/scenario/reset absent from other DEX files.
- iOS source/dependency closure: ordinary host excludes `core/mock`; mock host
  includes it and compiles. The actual Xcode shell phase was executed with a stub
  wrapper for all four environments and selects the mock framework only for MOCK.
  Existing graph/ATS checks and seven fail-closed build guards also passed.
- No ADB device is attached. `xcode-select` points to CommandLineTools and `xcrun
  simctl` is unavailable. **Native application walkthrough was not run.**
- One initial command accidentally used the host JDK 27 and failed configuration;
  all reported successful checks use JDK 17. A long-lived 4 GB daemon subsequently
  exhausted its JIT code cache during D8 (diagnostic: compilation disabled); that
  run was stopped and repeated with `-XX:ReservedCodeCacheSize=512m`. A new real
  repository upload regression initially exposed unsupported Ktor progress-wrapped
  bodies; the mock parser was corrected and the test passes. The session cache
  regression was also observed failing before its fix. No failure is counted as
  successful verification.
- Hosted CI was updated but not observed. Backend stays unchanged at `2ed9f2d`;
  no backend tests, shared database access or infrastructure operation was needed.

Reproduction (JDK 17, SDK 35, repository wrapper):

```sh
./gradlew :core:mock:testDebugUnitTest :core:domain:testDebugUnitTest \
  :core:datastore:testDebugUnitTest :core:network:testDebugUnitTest \
  :core:data:testDebugUnitTest :feature:vehicle_detail:testDebugUnitTest \
  :composeApp:assembleMockDebug :composeApp:lintMockDebug \
  :composeApp:assembleDevDebug :composeApp:lintDevDebug \
  :composeApp:assembleStageDebug :composeApp:lintStageDebug \
  :composeApp:assembleProdRelease :composeApp:lintProdRelease \
  compileKotlinIosSimulatorArm64 \
  -Pmotopark.stage.apiBaseUrl=https://stage.example.invalid \
  -Pmotopark.prod.apiBaseUrl=https://prod.example.invalid \
  --max-workers=2 '-Dorg.gradle.jvmargs=-Xmx4g -XX:ReservedCodeCacheSize=512m -Dfile.encoding=UTF-8'
./gradlew :composeApp:compileKotlinIosSimulatorArm64 -Pmotopark.mock=true
python3 scripts/check-android-environments.py
python3 scripts/check-mock-packaging.py
python3 scripts/check-ios-environments.py
```

The same-client role-switch test exposed a pre-existing Ktor bearer cache that
survived DataStore session changes. `HttpClientFactory` now invalidates cached
credentials before Auth adds headers when the token pair changes. A standalone
network regression and a mock user/admin/user walkthrough verify the correction;
existing refresh and foreign-origin protections also pass.
