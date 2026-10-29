#!/usr/bin/env python3
"""Fail on fixture drift between the two independent sibling repositories."""
from pathlib import Path
import hashlib

workspace = Path(__file__).resolve().parents[2]
paths = [workspace / 'vehicle-tracking-app/core/data/src/androidUnitTest/resources/contract/api.json',
         workspace / 'vehicle-tracking-backend/src/test/resources/contract/api.json']
contents = [p.read_bytes() for p in paths]
if contents[0] != contents[1]:
    raise SystemExit('Contract fixture copies differ; review and update both repositories.')
print('Contract fixtures match: SHA-256 ' + hashlib.sha256(contents[0]).hexdigest())
