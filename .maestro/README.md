# Maestro E2E Flows for Vehicle Tracking App

Tato složka obsahuje E2E testovací toky pro [Maestro](https://maestro.mobile.dev/) pokrývající deterministické průchody aplikací nad variantou `MOCK` (`cz.kudladev.vehicletracking.mock`).

## Struktura adresáře

```
.maestro/
├── config.yaml                         # Globální konfigurace a výchozí proměnné
├── env/
│   ├── mock.env                        # Proměnné prostředí pro CLI (--env-file)
│   └── mock.yaml                       # Proměnné prostředí ve formátu YAML
├── subflows/
│   ├── login_as_user.yaml              # Přihlášení standardního uživatele
│   ├── login_as_admin.yaml             # Přihlášení administrátora
│   └── logout.yaml                     # Odhlášení z menu
├── flows/
│   ├── smoke_login_and_browse.yaml     # Smoke průchod: login, katalog, detail, vyhledávání, menu, odhlášení
│   ├── smoke_admin_navigation.yaml     # Smoke průchod admina: login, admin prvky, správa výpůjček, odhlášení
│   ├── auth_validation_and_errors.yaml # Validace, neplatné přihlášení, registrační formulář
│   ├── reservation_lifecycle.yaml      # Výběr termínu v kalendáři a vytvoření rezervace
│   └── admin_protocol_handover.yaml    # Admin správa žádostí (čekající/schválené)
└── README.md
```

## Konfigurace prostředí (MOCK)

Všechny scénáře běží deterministicky nad in-memory sandboxem v modulu `core:mock`:
- **Package / App ID:** `cz.kudladev.vehicletracking.mock`
- **Uživatelský účet:** `user@example.invalid` / `Fixture-only-1!`
- **Administrátorský účet:** `admin@example.invalid` / `Fixture-only-1!`
- **Výchozí vozidlo:** ID `2147483648` (Škoda Octavia Combi)

## Spuštění a validace

### Požadavky
1. Nainstalované Maestro CLI (instalováno v `~/.maestro/bin/maestro`).
2. Pro spuštění testů: spuštěný Android emulátor nebo připojené zařízení s nainstalovanou MOCK variantou:
   ```bash
   ./gradlew :composeApp:installMockDebug
   ```

### Statická kontrola syntaxe toků (bez nutnosti zařízení)
```bash
maestro check-syntax .maestro/flows/smoke_login_and_browse.yaml
maestro check-syntax .maestro/flows/smoke_admin_navigation.yaml
maestro check-syntax .maestro/flows/auth_validation_and_errors.yaml
maestro check-syntax .maestro/flows/reservation_lifecycle.yaml
maestro check-syntax .maestro/flows/admin_protocol_handover.yaml
```

### Spuštění testů na zařízení/emulátoru
```bash
# Smoke test uživatele
maestro test --env-file .maestro/env/mock.env .maestro/flows/smoke_login_and_browse.yaml

# Smoke test administrátora
maestro test --env-file .maestro/env/mock.env .maestro/flows/smoke_admin_navigation.yaml

# Validace autentizace a registrace
maestro test --env-file .maestro/env/mock.env .maestro/flows/auth_validation_and_errors.yaml

# Životní cyklus rezervace
maestro test --env-file .maestro/env/mock.env .maestro/flows/reservation_lifecycle.yaml

# Kompletní sada
maestro test --env-file .maestro/env/mock.env .maestro/flows/
```
