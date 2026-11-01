#!/usr/bin/env python3
"""Inspect packaged APK identities and transport permissions without installing them."""
import json
import os
from pathlib import Path
import re
import subprocess

root = Path(__file__).resolve().parents[1]
sdk = Path(os.environ.get('ANDROID_HOME') or os.environ['ANDROID_SDK_ROOT'])
aapt = sdk / 'build-tools/35.0.0/aapt'
ids = set()
for environment, build_type in [('dev', 'debug'), ('mock', 'debug'), ('stage', 'debug'), ('prod', 'release')]:
    directory = root / f'composeApp/build/outputs/apk/{environment}/{build_type}'
    metadata = json.loads((directory / 'output-metadata.json').read_text())
    apk = directory / metadata['elements'][0]['outputFile']
    package = 'cz.kudladev.vehicletracking' + ('' if environment == 'dev' else '.' + environment)
    label = 'Motopark' + ('' if environment == 'prod' else ' ' + environment.upper())
    def dump(*args):
        return subprocess.check_output([str(aapt), 'dump', *args], text=True)
    badging = dump('badging', str(apk))
    assert metadata['applicationId'] == package
    assert f"package: name='{package}'" in badging
    assert f"application-label:'{label}'" in badging
    assert package not in ids
    ids.add(package)
    internet = "uses-permission: name='android.permission.INTERNET'" in badging
    assert internet == (environment != 'mock')
    manifest = dump('xmltree', str(apk), 'AndroidManifest.xml')
    assert re.search(r'android:allowBackup\([^\n]+\)=\(type 0x12\)0x0', manifest)
    # Release resource optimization can rename archive entries; resolve by resource name.
    resources = dump('--values', 'resources', str(apk))
    resource_path = re.search(
        r'resource 0x[0-9a-f]+ [^\n]+:xml/network_security_config:[^\n]*\n\s+\(string8\) "([^" ]+)"',
        resources,
    )
    assert resource_path, 'Missing packaged network security configuration'
    security = dump('xmltree', str(apk), resource_path.group(1))
    value = '0xffffffff' if environment == 'dev' else '0x0'
    assert f'cleartextTrafficPermitted=(type 0x12){value}' in security
    print(f'PASS: {environment}: {package}, {label}, INTERNET={internet}, cleartext={environment == "dev"}')
