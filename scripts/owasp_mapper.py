# uses python 3
# Utility script to change the mapping in a spec file to 
# corresponding the owasp ids replacing things in angular brackets <>
# Setup using
# pip install pyaml
# Usage:
# python owasp_mapper.py <spec_file_path>

import yaml
import sys
import os
import re

MODEL_FILE= 'testdata/model.yml'


filepath = sys.argv[-1]

with open(os.path.abspath(MODEL_FILE)) as f:
    model = yaml.load(f)
mapping = {}

for _k,_v in model.items():
    if isinstance(_v, dict):
        for k,v in _v.items():
            try:
                mapping[v['id']['locator']]=k
            except:
                pass
# print(mapping)

with open(filepath,'r') as f:
    content = f.read()
    content_copy = content
    match = None
    for match in re.findall('<(.*)>', content):
        print ("{} -> {}".format(match, mapping[match]))
        content_copy = re.sub('<{}>'.format(match), mapping[match], content_copy)
        print (content_copy)
with open(filepath, 'w+') as f:
    f.write(content_copy)