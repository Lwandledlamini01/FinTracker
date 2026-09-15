with open('app_chunk.js') as f:
    text = f.read()

import re

# Search for initial state or guest login button
print("=== GUEST / DEMO LOGIN ===")
for m in re.finditer(r'(?:guest|demo|sample|default)[^;]{10,150}', text, re.IGNORECASE):
    print("-", m.group(0))

# Search for goals initial array
for m in re.finditer(r'(?:title|name):["\'][^"\']+["\'],target:[0-9]+', text):
    print("Goal:", m.group(0))

