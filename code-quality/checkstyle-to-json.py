#!/usr/bin/env python

import hashlib
import json
import re
import sys
from argparse import ArgumentParser, FileType
from xml.etree import ElementTree

# Parse arguments
parser = ArgumentParser(description='Convert and print checkstyle from stdin to \
    json file with context.')
parser.add_argument('source', type=FileType('r'), help='Source checkstyle XML file path')
parser.add_argument('dest', type=FileType('w'), help='Destination JSON file path')
args = parser.parse_args()

# Gathered files data
items = []

# Severities match between Checkstyle and Code Quality
severities = {
    'error': 'major',
    'warning': 'minor',
    'info': 'info'
}

# Iterate over all files
for fileElement in ElementTree.parse(args.source).getroot():
    filepath = fileElement.attrib['name']

    # Iterate over all errors
    for errorElement in fileElement:
        line = int(errorElement.attrib['line'])
        message = errorElement.attrib['message']
        filename = re.sub('.*?src', 'src', filepath)
        severity = severities[errorElement.attrib['severity']]

        items.append({
            'type': 'issue',
            'categories': 'Style',
            'description': message + ' [' + re.sub('.*\.', '', errorElement.attrib['source']) + ']',
            'severity': severity,
            'fingerprint': hashlib.md5((message + filename + str(line)).encode('utf-8')).hexdigest(),
            'location': {
                'path': filename,
                'lines': {
                    'begin': line
                }
            }
        })

# Sort by severity
severities_order = ['major', 'minor', 'info']
items.sort(key=lambda i: severities_order.index(i['severity']))

# Print gathered files in json format
args.dest.write(
    json.dumps(items, indent=4, sort_keys=False)
)

if items:
    sys.exit(1)
