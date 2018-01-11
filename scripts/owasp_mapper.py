# uses python 3
# Utility script to change the mapping in a spec file to 
# corresponding the owasp ids replacing things in angular brackets <>
# Setup using
# pip install pyaml
# Usage:
# python owasp_mapper.py <spec_file_path>

import yaml
with yaml.load('../testdata/model.yml') as f:
    model = yaml.load(f)
mapping = {}
def get_map(d):
    global mapping
    for k, v in d.items():
        if isinstance(v, dict):
            parent = v
            get_map(v)
        else:
            print (d)
