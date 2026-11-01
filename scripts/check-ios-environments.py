#!/usr/bin/env python3
"""Check the actual Xcode configuration graph and fail-closed hosted build guard.
Requires macOS plutil, not Xcode or a simulator. Never starts the application.
"""
import json
import os
from pathlib import Path
import plistlib
import subprocess
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
project = root / 'iosApp/iosApp.xcodeproj'
subprocess.run(['plutil', '-lint', str(project / 'project.pbxproj')], check=True)
graph = json.loads(subprocess.check_output(
    ['plutil', '-convert', 'json', '-o', '-', str(project / 'project.pbxproj')]))['objects']
ids = set()
for environment in ('Dev', 'Mock', 'Stage', 'Prod'):
    scheme = ET.parse(project / f'xcshareddata/xcschemes/Motopark-{environment}.xcscheme')
    assert scheme.find('LaunchAction').get('buildConfiguration') == environment + 'Debug'
    assert scheme.find('ArchiveAction').get('buildConfiguration') == environment + 'Release'
    for kind in ('PBXProject', 'PBXNativeTarget'):
        owner = next(v for v in graph.values() if v.get('isa') == kind)
        configs = [graph[c] for c in graph[owner['buildConfigurationList']]['buildConfigurations']]
        for mode in ('Debug', 'Release'):
            config = next(c for c in configs if c['name'] == environment + mode)
            assert graph[config['baseConfigurationReference']]['path'] == environment + '.xcconfig'
            assert config['buildSettings']['KOTLIN_FRAMEWORK_BUILD_TYPE'] == mode.upper()
            if kind == 'PBXNativeTarget':
                plist = root / 'iosApp' / config['buildSettings']['INFOPLIST_FILE']
                info = plistlib.loads(plist.read_bytes())
                assert info['MotoparkEnvironment'] == '$(MOTOPARK_ENVIRONMENT)'
                assert info['CFBundleIdentifier'] == '$(PRODUCT_BUNDLE_IDENTIFIER)'
                assert info['CFBundleDisplayName'] == '$(APP_NAME)'
                if environment != 'Dev':
                    assert 'NSAppTransportSecurity' not in info
    xcconfig = (root / f'iosApp/Configuration/{environment}.xcconfig').read_text()
    assert f'MOTOPARK_ENVIRONMENT={environment.upper()}' in xcconfig
    bundle = next(line for line in xcconfig.splitlines() if line.startswith('BUNDLE_ID='))
    assert bundle not in ids
    ids.add(bundle)
    if environment in ('Stage', 'Prod'):
        assert 'MOTOPARK_API_HOST=\n' in xcconfig
        assert 'MOTOPARK_API_SCHEME=\n' in xcconfig
# Run the same shell guard Xcode executes, with no service access.
guard = root / 'iosApp/Configuration/validate-environment.sh'
for environment, scheme, host, port, expected in [
    ('DEV', 'http', '127.0.0.1', '8080', True),
    ('MOCK', '', '', '', True),
    ('STAGE', '', '', '', False),
    ('PROD', 'http', 'localhost', '8080', False),
    ('STAGE', 'https', 'stage.example.invalid', '443', True),
    ('PROD', 'https', 'prod.example.invalid', '443', True),
    ('UNKNOWN', 'https', 'example.invalid', '443', False),
]:
    result = subprocess.run(['/bin/sh', str(guard)], capture_output=True, env={
        **os.environ, 'MOTOPARK_ENVIRONMENT': environment, 'MOTOPARK_API_SCHEME': scheme,
        'MOTOPARK_API_HOST': host, 'MOTOPARK_API_PORT': port,
    })
    assert (result.returncode == 0) == expected, (environment, result.stderr)
assert 'validate-environment.sh' in next(
    v['shellScript'] for v in graph.values() if v.get('name') == 'Compile Kotlin Framework')
print('PASS: four shared schemes, eight paired configurations, bundle IDs, ATS and seven build guard cases')

# Execute the checked-in build phase against a stub Gradle wrapper: only MOCK may
# select mock sources/dependencies. No framework linking or native runtime claimed.
import tempfile
import shutil
phase = next(v['shellScript'] for v in graph.values() if v.get('name') == 'Compile Kotlin Framework')
with tempfile.TemporaryDirectory(prefix='motopark-xcode-') as directory:
    sandbox = Path(directory)
    configuration = sandbox / 'iosApp/Configuration'
    configuration.mkdir(parents=True)
    shutil.copy2(guard, configuration / guard.name)
    wrapper = sandbox / 'gradlew'
    wrapper.write_text('#!/bin/sh\nprintf "%s\\n" "$@" > "$CAPTURE"\n')
    wrapper.chmod(0o755)
    for environment in ('MOCK', 'DEV', 'STAGE', 'PROD'):
        capture = sandbox / 'arguments'
        subprocess.run(['/bin/sh', '-c', phase], check=True, env={
            **os.environ, 'SRCROOT': str(sandbox / 'iosApp'),
            'OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED': 'NO', 'CAPTURE': str(capture),
            'MOTOPARK_ENVIRONMENT': environment, 'MOTOPARK_API_SCHEME': 'https',
            'MOTOPARK_API_HOST': 'example.invalid', 'MOTOPARK_API_PORT': '443',
        })
        assert capture.read_text().splitlines() == [
            ':composeApp:embedAndSignAppleFrameworkForXcode',
            '-Pmotopark.mock=' + ('true' if environment == 'MOCK' else 'false'),
        ], environment
print('PASS: actual Xcode build phase selects mock framework only for MOCK')
