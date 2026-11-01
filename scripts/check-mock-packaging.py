#!/usr/bin/env python3
"""Verify mock classes/engine/scenario/reset are absent from packaged non-mock DEX."""
from pathlib import Path
from zipfile import ZipFile
root = Path(__file__).resolve().parents[1]
for variant, mode in [('mock','debug'),('dev','debug'),('stage','debug'),('prod','release')]:
    apk = next((root / f'composeApp/build/outputs/apk/{variant}/{mode}').glob('*.apk'))
    with ZipFile(apk) as archive:
        dex = b''.join(archive.read(n) for n in archive.namelist() if n.endswith('.dex'))
    for marker in [b'Lcz/kudladev/vehicletracking/mock/MockServer;', b'Lio/ktor/client/engine/mock/MockEngine;', b'LOST_UPLOAD_RESPONSE', b'mock-archive-']:
        assert (marker in dex) == (variant == 'mock'), (variant, marker)
    print(f'{variant}: mock implementation {"present" if variant == "mock" else "absent"}')
