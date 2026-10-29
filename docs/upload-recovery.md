# Obnova uploadů — 2026-10-29

Navazuje na původní upload ověření ve workspace docs/verification/2026-10-29/uploads.md. Změny jsou lokální v klientském
repozitáři; backend, wire kontrakt a databázové schéma se nemění.

## Implementované chování

- Android i iOS používají `PersistentUploadQueue`, stejný deník `UploadRecord`,
  trvalé UUID a mapování stavů do `UploadTask`. UUID označuje přenos, serverové
  ID označuje potvrzenou fotografii; tyto identity se nezaměňují.
- Fotografie má soubor podle UUID. Dvě fotografie stejné pozice se nepřepisují.
  Vozidla používají `vehicle_image_upload_<id>`, protokoly
  `tracking_image_upload_<trackingId>_<state>`. Obě protokolové overload cesty
  mají totožné chování. Obrazovka vytvoření vozidla sleduje pouze svůj cíl.
- Android ukládá metadata i bytes přes AtomicFile do files/image-uploads a
  spouští `DurableImageWorker` přes WorkManager s podmínkou připojení.
  URL importy mají také trvalá metadata. iOS dál používá atomické Foundation
  zápisy do Application Support/image-uploads a coroutine za života aplikace.
- Před HTTP se trvale uloží stav UPLOADING. Obnovené QUEUED i UPLOADING jsou
  konzervativně FAILED k prověření, bez automatického POSTu. Android worker smí
  začít pouze QUEUED záznam; opakované doručení FAILED/COMPLETED nic neposílá.
  Ani operační restart workeru nevyvolává `Result.retry()`.
- Zrušení ruší konkrétní UUID a zachovává bytes. Retry čeká na ukončení starého
  jobu; souběžné kliknutí nevytvoří druhý přenos. Čištění stavu obrazovkou pouze
  odstraní dokončené úspěchy, nezruší upload jiné obrazovky.
- Fotografie se maže až po trvalém zápisu potvrzení. Chyba HTTP, nejednoznačný
  výsledek, zrušení či chyba disku nejsou úspěch. Poškozená metadata se
  karanténují; osamocený JPG se objeví jako zachovaná fotografie bez cíle.

## Obnovovací UI a duplicity

Společné UI v kořeni aplikace nabízí „Přenosy fotografií“, nezávisle na původní
obrazovce. Ukazuje UUID, cíl, pozici, stav, chybu, lokální náhled a zrušení.
Stávající upload dialog vozidla lze opustit bez zrušení přenosů.

„Ověřit na serveru“ provádí pouze GET rodiče a jeho fotografií. Potvrzení vyžaduje
právě jednu fotografii stejné pozice s přesně shodnými bytes, platným ID a URL.
Nula nebo více shod nezpůsobí smazání ani POST. Síťová chyba zachová lokální data.
URL import bez lokálních bytes nelze touto metodou automaticky potvrdit.

„Odeslat znovu…“ vyžaduje výslovné potvrzení uživatele, že prověřil server,
vyčkal na původní přenos a fotografie chybí. Nejde o automatický retry.
**Bez serverové idempotence nelze zaručit absenci duplicit při ručním opakování:**
původní požadavek může ještě doběhnout nebo uživatel může chybně vyhodnotit stav.
UI tuto skutečnost uvádí. Automatické opakování nejistého POSTu není povoleno.
Bytes se při opakování zachovávají pod původním UUID až do potvrzení.

Staré Android worker třídy zůstávají kvůli uloženým WorkManager požadavkům, ale
už neposílají POST. Staré `temp_image_*.jpg` se importují k prověření bez
odstranění originálu; marker brání opakovanému importu. Z deterministického
protokolového názvu lze obnovit rezervaci/stav/pozici. Timestampový název neobsahuje
cíl: UI zobrazí náhled a chybu, opakování je zakázáno a je nutná ruční obnova ze
soukromého úložiště vývojového zařízení. Smazání až po potvrzení zahrnuje i známý
legacy originál. Původní přepsané nebo již ztracené soubory obnovit nelze.

## Ověření a hranice důkazů

Příkaz (JDK 17 / Android SDK 35 podle klientského README):

```sh
bash ./gradlew :core:data:testDebugUnitTest :composeApp:compileKotlinIosSimulatorArm64 :composeApp:assembleDebug :composeApp:lintDebug --console=plain
```

- `core/data`: 31 testů, 0 failures/errors. Z toho fronta 15 a upload transport 10.
  Nové regresní scénáře: duplicitní UUID bez přepsání bytes, worker redelivery,
  restart QUEUED/UPLOADING/FAILED/CANCELED bez replaye, in-flight cancel a zákaz
  retry před dokončením zrušení, explicitní single-flight retry, dvě fotografie
  stejné pozice, potvrzení před smazáním, bezpečné čištění obrazovkou a GET-only
  byte reconciliation s nulou/jednou/více shodami a odlišnými bytes.
- Android assembleDebug a lintDebug prošly. Lint 0 chyb / 52 varování.
  První běh odhalil chybějící volitelný camera feature; doplněno bez potlačení lint.
- Celá iOS Kotlin kompilace IosSimulatorArm64 prošla. KLIB savedstate varování
  přetrvávají. [Log kontrol](upload-recovery-checks.log).
- `xcrun xcodebuild -version` selhal (exit 72), protože aktivní developer directory
  je CommandLineTools, nikoli plné Xcode. [Důkaz](upload-recovery-xcode.log).
  Framework link ani SwiftUI host nebyly tímto krokem sestavené/spuštěné.
- Backendové testy se nespouštěly: backend se nemění a nepoužila se žádná DB.

**Neověřeno na zařízení:** skutečný Android WorkManager restart/force-stop,
AtomicFile a import legacy souborů, Compose náhledy/dialogy, iOS Foundation I/O,
permission flow, start DI a suspend/relaunch. Testy společné fronty používají
paměťový store a MockEngine; nejsou důkazem nativního filesystemu či systémového
plánování. Před uzavřením runtime části roadmapy provést na obou platformách:
chybu serveru, zrušení během přenosu, ukončení procesu před odpovědí, relaunch,
GET ověření již uložené fotografie a výslovný retry chybějící fotografie.

**iOS omezení:** background URLSession není implementována; běh při suspend nebo
po ukončení procesu není garantován. Hotová je obnova deníku a UI při dalším
spuštění. Nativní background přenos, framework link a runtime ověření zůstávají
otevřené. Obsah serverové fotografie musí zůstat byte-for-byte stejný pro
reconciliation; případná budoucí serverová transformace vyžaduje jiný kontrakt.

## Navazující kontrola a opravy

- Recovery rezervuje UUID po celou dobu GET ověřování až po trvalé potvrzení.
  Během něj retry ani druhé recovery stejného UUID nezačne. Recovery odmítá
  aktivní i dosud dobíhající zrušený job. Zámek se uvolní i při chybě/zrušení GET.
- Před POST se do deníku uloží seznam existujících serverových ID. Recovery
  vylučuje tato ID a vyžaduje jedno nové ID, správný cíl/pozici/URL a přesné bytes.
  Konfliktní metadata stejného ID se odmítají. Legacy záznam bez tohoto seznamu
  nelze automaticky potvrdit; soubor zůstává k ručnímu prověření.
- I úspěšná POST odpověď musí označovat novou fotografii správné pozice;
  u lokální fotografie se následným GET ověřují bytes před odstraněním souboru.
  Nedostupný GET znamená nejistý výsledek a zachování důkazů, bez replaye POST.
- Po návratu transportu se kontroluje cancellation a aktuální stav deníku.
  Pozdní výsledek zrušeného POSTu nesmí změnit zrušení na úspěch a smazat bytes.
- Ruční retry zůstává explicitní riziková akce s potvrzením. Ukončení lokálního
  jobu nedokazuje ukončení serverové transakce. Ani seznam ID a shoda bytes
  nedokazují autorství fotografie při souběžném identickém uploadu jiného klienta.
  Bez serverové idempotence nelze garantovat právě jeden zápis.

Regresní ověření: 36 core/data testů (18 fronta, 12 transport, 6 kontrakt),
Android assembleDebug/lintDebug a iOS compileKotlinIosSimulatorArm64.
ADB hlásí prázdný seznam zařízení; v obvyklých SDK umístěních není emulator
ani AVD. Android restart a recovery UI nebyly nativně ověřeny.
iOS filesystem, UI, suspend/relaunch, framework link/start a background URLSession
zůstávají výslovně neověřené/neimplementované podle výše uvedených omezení.

Kopie tohoto dokumentu je verzovaná v klientském `docs/upload-recovery.md`;
workspace roadmapa a původní dokument leží mimo Git repozitáře.

## Idempotency keys (2026-10-29)

Every Android/iOS transfer now sends the journal's durable UUID as
`Idempotency-Key`, including URL imports. Retrying or restoring a journal never
allocates a new operation ID. The new backend returns `X-Upload-Image-Id`; this
allows acknowledgement of a replay even if the preflight GET already contains
the photo. Parent, position, URL and local-byte verification still apply, and
local bytes are deleted only after durable completion. A conflicting operation
returns 409 and retains local evidence.

Older servers ignore the new optional header and return the established parent
JSON; the client keeps the previous new-photo checks as fallback. Older clients
can upload to the new backend without a key. Because the client cannot assume a
server upgrade, automatic POST retries/replays remain disabled. Interrupted jobs
still require explicit recovery/retry; keyed retries against the new backend are
safe after a lost response or restart. Old journal entries that already have a
UUID use it unchanged; historical uploads sent before server-side idempotency
cannot acquire protection retroactively and can still duplicate on manual replay.

The regression test `lostResponseAndRestartReuseDurableKeyAndAcceptExistingPhoto`
commits a simulated HTTP upload, loses its response, retains the bytes, serializes
and reloads the journal, explicitly retries, checks the identical key, accepts the
existing photo ID and only then removes bytes. Native filesystem/process and iOS
background execution limitations described above remain unverified.
