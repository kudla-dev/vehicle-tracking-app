# Maestro E2E Flows for Vehicle Tracking App

Tato složka obsahuje E2E testovací toky pro [Maestro](https://maestro.mobile.dev/) pokrývající deterministické průchody aplikací nad variantou `MOCK` (`cz.kudladev.vehicletracking.mock`).

## Struktura adresáře

```
.maestro/
├── config.yaml                     # Globální konfigurace a výchozí proměnné
├── env/
│   ├── mock.env                    # Proměnné prostředí pro CLI (--env-file)
│   └── mock.yaml                   # Proměnné prostředí ve formátu YAML
├── subflows/
│   ├── login_as_user.yaml          # Přihlášení standardního uživatele
│   ├── login_as_admin.yaml         # Přihlášení administrátora
│   └── logout.yaml                 # Odhlášení z menu
├── flows/
│   ├── smoke_login_and_browse.yaml # Smoke průchod uživatele: login, katalog, detail, vyhledávání, menu, odhlášení
│   └── smoke_admin_navigation.yaml # Smoke průchod administrátora: login, admin prvky, správa výpůjček, odhlášení
└── README.md
```

## Konfigurace prostředí (MOCK)

Všechny scénáře běží deterministicky nad in-memory sandboxem v modulu `core:mock`:
- **Package / App ID:** `cz.kudladev.vehicletracking.mock`
- **Uživatelský účet:** `user@example.invalid` / `Fixture-only-1!`
- **Administrátorský účet:** `admin@example.invalid` / `Fixture-only-1!`
- **Výchozí vozidlo:** ID `2147483648` (Škoda Octavia Combi)

## Spuštění testů

### Požadavky
1. Nainstalované Maestro CLI (`curl -Ls "https://get.maestro.mobile.dev" | bash`).
2. Spuštěný Android emulátor nebo připojené zařízení s nainstalovanou MOCK variantou:
   ```bash
   ./gradlew :composeApp:installMockDebug
   ```

### Příkazy pro spuštění

Spuštění základního smoke testu pro uživatele:
```bash
maestro test --env-file .maestro/env/mock.env .maestro/flows/smoke_login_and_browse.yaml
```

Spuštění smoke testu pro administrátora:
```bash
maestro test --env-file .maestro/env/mock.env .maestro/flows/smoke_admin_navigation.yaml
```

Spuštění všech testovacích toků najednou:
```bash
maestro test --env-file .maestro/env/mock.env .maestro/flows/
```
