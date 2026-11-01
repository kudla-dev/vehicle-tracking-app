# Maestro UI Flow Matrix & Screen Inventory

Dokument shrnuje inventář funkčních uživatelských průchodů, matici pokrytí podle platforem, rolí (standardní uživatel vs. administrátor) a prostředí (lokální stavový `MOCK` sandbox vs. reálný backend), s explicitním odlišením plně funkčních obrazovek od dosavadních placeholderů.

---

## 1. Inventář obrazovek a stav implementace

| Obrazovka / Modul | Účel | Stav implementace | Klíčové selektory (`testTag`) |
| :--- | :--- | :--- | :--- |
| **WelcomeScreen** (`feature:onboarding`) | Úvodní obrazovka s volbou přihlášení či registrace | **Plně funkční** | `auth:welcome_login_button`<br>`auth:welcome_register_button` |
| **LoginScreen** (`feature:onboarding`) | Formulář přihlášení (e-mail, heslo, validace) | **Plně funkční** | `auth:login_email_input`<br>`auth:login_password_input`<br>`auth:login_submit_button`<br>`auth:login_password_toggle`<br>`auth:login_back_button` |
| **RegisterScreen** (`feature:onboarding`) | Registrační formulář (jméno, e-mail, telefon, heslo) | **Plně funkční** | `auth:register_first_name_input`<br>`auth:register_last_name_input`<br>`auth:register_email_input`<br>`auth:register_phone_input`<br>`auth:register_password_input`<br>`auth:register_confirm_password_input`<br>`auth:register_submit_button` |
| **BottomBar** (`composeApp`) | Hlavní navigace aplikace (5 záložek) | **Plně funkční** | `nav:bottom_bar`<br>`nav:bottom_bar:vehicles`<br>`nav:bottom_bar:favourites`<br>`nav:bottom_bar:tracking`<br>`nav:bottom_bar:history`<br>`nav:bottom_bar:menu` |
| **VehicleListScreen** (`feature:vehicles`) | Katalog vozidel (mřížka / seznam, filtry) | **Plně funkční** | `vehicles:grid`<br>`vehicles:item:<id>`<br>`vehicles:search_action`<br>`vehicles:toggle_view_action`<br>`vehicles:add_button` (pouze admin)<br>`vehicles:empty_view`<br>`vehicles:error_view` |
| **VehicleDetailScreen** (`feature:vehicle_detail`) | Detail vozidla, parametry, kalendář rezervací | **Plně funkční** | `vehicle_detail:title`<br>`vehicle_detail:calendar`<br>`vehicle_detail:reserve_button`<br>`vehicle_detail:back_button`<br>`vehicle_detail:admin_edit_vehicle` *(UI přítomno, akce no-op)* |
| **SearchScreen** (`feature:search`) | Vyhledávání vozidel s historií dotazů | **Plně funkční** | `search:input`<br>`search:cancel_button`<br>`search:query_item`<br>`search:result:<id>` |
| **TrackingScreen** (`feature:tracking`) | Přehled aktuálně probíhající výpůjčky | **Plně funkční** | `tracking:vehicle_header`<br>`tracking:current_state`<br>`tracking:time_remaining`<br>`tracking:confirm_return_button`<br>`tracking:empty_view`<br>`tracking:error_view` |
| **FavouriteScreen** (`feature:favourite`) | Oblíbená vozidla | ⚠️ **PLACEHOLDER** *(pouze prázdný Scaffold a titulek)* | `favourites:root` |
| **HistoryScreen** (`feature:history`) | Paginovaná historie proběhlých výpůjček | **Plně funkční** | `history:item:<id>`<br>`history:empty_view`<br>`history:error_view` |
| **MenuScreen** (`feature:menu:main`) | Uživatelský profil, navigace administrátora, odhlášení | **Částečně funkční** *(viz níže)* | `menu:user_card`<br>`menu:logout_button`<br>`menu:admin_trackings_*` (funkční)<br>`menu:personal_data` *(placeholder)*<br>`menu:change_password` *(placeholder)*<br>`menu:admin_manage_users` *(placeholder)*<br>`menu:admin_manage_vehicles` *(placeholder)*<br>`menu:admin_tracking_history` *(placeholder)* |
| **ManageTrackings** (`feature:menu:manage_trackings`) | Seznam výpůjček ke schválení, předání a vrácení | **Plně funkční** | `admin:trackings_back_button`<br>`admin:trackings_empty_view`<br>`admin:tracking_item:<id>` |
| **TrackingDetail** (`feature:menu:tracking_detail`) | Detail výpůjčky pro administrátora, schválení/zamítnutí | **Plně funkční** | `admin:tracking_detail_back_button`<br>`admin:tracking_approve_button`<br>`admin:tracking_reject_button`<br>`admin:tracking_pickup_button`<br>`admin:tracking_return_button` |
| **Protocols** (`feature:menu:protocols`) | Předávací a návratový protokol (4 fotografie, stav tachometru, poznámka) | **Plně funkční** | `protocols:back_button`<br>`protocols:camera_capture_button`<br>`protocols:retake_button`<br>`protocols:next_button`<br>`protocols:tachometer_input`<br>`protocols:notes_input`<br>`protocols:submit_button` |

---

## 2. Matice pokrytí uživatelských toků

### Role: Standardní uživatel (`USER`)

| Průchod (Flow) | Stav obrazovek | MOCK prostředí | Reálný backend | Android | iOS |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **FL-01: Registrace nového uživatele** | Plně funkční | ✅ Podporováno | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-02: Přihlášení a odhlášení uživatele** | Plně funkční | ✅ **Implementováno v Maestro** (`smoke_login_and_browse.yaml`) | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-03: Prohlížení katalogu a detailu vozidla** | Plně funkční | ✅ **Implementováno v Maestro** (`smoke_login_and_browse.yaml`) | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-04: Vyhledávání vozidel** | Plně funkční | ✅ **Implementováno v Maestro** (`smoke_login_and_browse.yaml`) | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-05: Rezervace vozidla v kalendáři** | Plně funkční | ✅ Podporováno (mock sandbox) | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-06: Zobrazení a potvrzení vrácení v TrackingScreen** | Plně funkční | ✅ Podporováno (mock sandbox) | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-07: Paginovaná historie výpůjček** | Plně funkční | ✅ Podporováno | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-08: Správa oblíbených položek** | ⚠️ Placeholder | ❌ Neimplementováno v UI | ❌ Neimplementováno v UI | ❌ Blokováno UI | ❌ Blokováno UI |
| **FL-09: Editace osobních údajů a hesla** | ⚠️ Placeholder | ❌ Neimplementováno v UI | ❌ Neimplementováno v UI | ❌ Blokováno UI | ❌ Blokováno UI |

### Role: Administrátor (`ADMIN`)

| Průchod (Flow) | Stav obrazovek | MOCK prostředí | Reálný backend | Android | iOS |
| :--- | :---: | :---: | :---: | :---: | :---: |
| **FL-10: Přihlášení administrátora a kontrola oprávnění** | Plně funkční | ✅ **Implementováno v Maestro** (`smoke_admin_navigation.yaml`) | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-11: Schválení / zamítnutí rezervace** | Plně funkční | ✅ Podporováno (mock sandbox) | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-12: Provedení předávacího protokolu (Pickup)** | Plně funkční | ✅ Podporováno (mock sandbox s fotografiemi) | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-13: Provedení protokolu o vrácení (Return)** | Plně funkční | ✅ Podporováno (mock sandbox s fotografiemi) | ✅ Podporováno | ✅ Připraveno | ✅ Připraveno |
| **FL-14: Přidání nového vozidla** | ⚠️ Rozhraní `add_button` je přítomno, formulář chybí | ❌ Neimplementováno v UI | ❌ Neimplementováno v UI | ❌ Blokováno UI | ❌ Blokováno UI |
| **FL-15: Správa uživatelů** | ⚠️ Placeholder | ❌ Neimplementováno v UI | ❌ Neimplementováno v UI | ❌ Blokováno UI | ❌ Blokováno UI |

---

## 3. Selektorová strategie pro automatizaci

1. **Android `testTagsAsResourceId`:**
   - Kořenové okno v `MainActivity.kt` aplikuje:
     ```kotlin
     App(
         modifier = Modifier.semantics {
             testTagsAsResourceId = true
         }
     )
     ```
   - Tím se všechny Compose `Modifier.testTag("...")` přímo mapují na nativní Android resource-id selektory, které Maestro transparentně identifikuje pomocí `id: "..."`.
2. **Konzistentní jmenná konvence selektorů:**
   - `auth:<screen>_<element>` (např. `auth:login_email_input`)
   - `nav:bottom_bar:<tab>` (např. `nav:bottom_bar:vehicles`)
   - `<domain>:<element>:<id>` (např. `vehicles:item:2147483648`, `history:item:42`)
   - `admin:<domain>_<action>` (např. `admin:tracking_approve_button`)
   - `protocols:<step>_<action>` (např. `protocols:camera_capture_button`, `protocols:tachometer_input`)

---

## 4. Omezení prostředí a verifikace

- **Maestro CLI:** Na daném testovacím uzlu není binárka `maestro` nainstalována v PATH. Toky jsou vytvořeny dle oficiální specifikace Maestro a jsou připraveny k okamžitému spuštění na CI či lokální pracovní stanici s nainstalovaným Maestro a spuštěným emulátorem/zařízením.
- **Nativní runtime zařízení:** V prostředí nejsou k dispozici žádné Android emulátory ani připojená reálná zařízení (žádné AVD, žádné ADB zařízení).
- **iOS simulátor:** V prostředí je přítomen pouze Xcode CommandLineTools, nikoliv plný Xcode s `simctl`. Sestavení iOS aplikace je ověřeno na úrovni KMP Kotlin kompilace (`compileKotlinIosSimulatorArm64`).
