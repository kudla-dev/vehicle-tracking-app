# API fixtures and ID compatibility — 2026-10-29

Fixtures are synthetic, sanitized examples, verified against the current serializers
and disposable PostgreSQL HTTP responses. They are not a capture of a deployed
service. Names, email/phone, image hosts, UUIDs and timestamps are public test data;
token strings are deliberately invalid. No real credentials or records belong here.

## Inventory and decisions

| Contract | Backend | Client after this change | Wire / notes |
| --- | --- | --- | --- |
| Vehicle ID, brand ID, request/filter/path IDs | Long | Long (vehicle response/domain ID remains nullable for unsaved forms) | JSON integer or decimal URL parameter, never a quoted JSON number |
| Tracking/user/image ID | UUID internally; DTO UUID or String | String | UUID text; no width change |
| Tracking-log identity | tracking UUID + state | trackingId + state | Composite DB key unchanged |
| Upload operation ID | UUID | UUID text in journal/header | `Idempotency-Key`; existing journal schema unchanged |
| Vehicle type ID | Int | No active matching consumer | Backend `VehicleTypeDTO`, not a reason to widen all integers |
| License ID | String | String | `type`, not a numeric ID |
| State count | Long | Long through repository, state and badge | Bare JSON integer; previous client Int was also too narrow |
| Mileage, image position, page and size | Int (some mileage/position nullable) | Int with corresponding nullability | Not IDs; deliberately unchanged |
| Login/register/refresh/token pair | Matching fields | Matching fields | Fixtures verify shapes, not token validity or registration policy |
| User | Required scalar fields | Same + optional profilePicture=null | Server does not provide profilePicture |
| Vehicle create | driverLicense: object collection | List of `{type}` objects | Singular request key; response uses plural `driverLicenses` |
| Brand/image/scrape | imageURL / url / driverLicense | Same wire keys | Scrape maps driverLicense to local drivingLicenses |
| Tracking | UUID, nested vehicle/user, Instant strings, nullable mileage, logs array | Strings parsed to dates; nullable mileage | Empty logs preserved; no invented state |
| Tracking log | nullable actor/date/message/photos DTO | Nullable actor/date/message; null photos map to empty list | DB assigned_at is NOT NULL; nullable-date fixture is DTO compatibility, not current stored data |
| Errors | error/message/path/status/timestamp | Tolerant envelope parser, status from HTTP | Unknown/missing JSON fields and non-JSON gateways supported by network tests |

Backend still requires vehicle IDs in responses. Client accepts explicit null for
unsaved-model compatibility, but the response `id` field is not defaulted if missing.
Required nullable tracking mileage keys are likewise not silently invented. UUIDs,
state casing, image/log ordering, JSON field names and numeric representation are
unchanged. The image upload response remains the whole parent, not ImageResponse.

## Fixtures and tests

The exact same `api.json` is committed in these repository-relative locations:

- App: `core/data/src/androidUnitTest/resources/contract/api.json`.
- Backend: `src/test/resources/contract/api.json`.

Each repository can run its tests independently. With sibling checkouts, run
`python3 scripts/check-contract-fixtures.py`; it fails if either file is absent or
if their bytes differ. Update both copies intentionally and review the diff; tests
do not regenerate expected payloads from the DTO being tested.

Client `SharedFixtureTest` exercises real repositories/serializers through MockEngine:
large and legacy IDs, domain mapping, numeric request encoding, auth shapes, empty
logs, null actor/date/photos/mileage, query names and page origins, empty pages,
204 (nullable/Unit success, required body failure), 400/401/403/404/409/500 envelopes,
HTML gateway fallback, successful parent upload acknowledgement with byte checking,
and terminal upload 409 with exactly one POST. Existing queue tests cover evidence
retention and recovery. Shared fixtures run on the JVM Android unit-test target;
common production code also compiles for iOS, but these are not native iOS tests.

Backend `VehicleTrackingApplicationTests` uses its existing dynamically isolated
PostgreSQL container and loopback HTTP server. `ContractFixtures` checks the actual
Spring ObjectMapper, all listed request/response shapes and Long boundaries. HTTP
checks create vehicles from the fixture request, read an ID above Int.MAX_VALUE,
filter/page it, bind a large vehicleId in booking JSON and queries, check history
page semantics, 204, empty stored logs, and upload response/photo bytes. Normalization
only replaces generated UUIDs, image URLs and error timestamps/paths with fixture
values; response fields are not dropped. Multipart idempotency tests compare both
vehicle and tracking 409 bodies to the shared fixture. 400/401/403/404 are checked
through HTTP; 500 is checked through the real exception advice with an internal
exception message to verify redaction. The HTML gateway is consumer-only.

Boundary matrix: 7, 2147483647, 2147483648 and Long.MAX_VALUE are decoded on both
sides; client repository mapping is exercised at each boundary. Real DB/HTTP tests
use vehicle 2147483648 and brand 2147483649. The old Int decoder is explicitly
asserted to reject 2147483648. No rounding, truncation or ID remapping is permitted.

## Pagination fix discovered by fixtures

A full vehicle page (`size=1`) previously triggered a broken auto-derived native
count query and returned 500. The repository now returns a paged List directly:
the service/API never exposed total counts, so there is no wire envelope change.
An ID tie-breaker after search relevance makes equal-ranked rows stable across pages.
The regression covers two full pages followed by an empty page. Vehicles remain
1-based; trackings/history remain 0-based and use `size`, not `pageSize`.

## Older clients and rollout

There is no confirmed released-version/support inventory; the product is currently
development-only. These are compatibility guarantees for source contracts, not a
claim that any unspecified historical binary was run:

- New Long clients accept existing small integer IDs and old server responses.
- Old Int clients can read vehicles only while every returned vehicle ID fits Int.
  A single large ID in a nested tracking or list can make the whole response fail.
  Old Int counter consumers have the same limit. Upgrade clients before exposing
  large IDs/counts; do not reset sequences or truncate/remap identifiers as a workaround.
- The server wire format is unchanged; no API version bump or DB migration is
  required for this widening. Existing DB bigint/UUID/composite IDs are preserved.
- Upload journals and WorkManager data already stored vehicle IDs as Long; no
  journal migration or deletion is needed. JSON numeric persisted data stays numeric.
- Typed vehicle navigation changes from Int? to Long?. A saved Android navigation
  back stack from an old binary is not guaranteed to restore after upgrade. Start
  a fresh navigation stack for this development upgrade. An in-place production
  upgrade would need a restore/migration test on devices before a support promise.
  Native Android/iOS runtime restoration has not been verified here.
- Pre-fix clients sending string driverLicense or pageSize/singular driverLicense
  query parameters remain unsupported for those operations; no legacy aliases were
  introduced. 204/null/error tolerance depends on the client revision.
- Old clients without Idempotency-Key remain accepted by the server, but do not gain
  replay protection. New keyed clients against old servers must not assume idempotence.
  The separate upload-idempotency DB migration remains required when rolling out that
  previously introduced feature; this ID change adds no migration.

No schema, credentials, deployed services or shared database were changed. Fixtures
are versioned in both repositories; the workspace roadmap is outside both Git roots.

## Verified result

2026-10-29: 46 core/data and 13 core/network tests passed; all 21 backend tests
passed on disposable PostgreSQL. Android assemble/lint passed (0 errors, 52 warnings)
and full iOS simulator Kotlin compilation passed. Native runtime/restore was not run.
Fixture SHA-256: `46240bba71bec2d72b869db44f38fb9380e7c437f014cbd4d2ec0a3e48809634`.
