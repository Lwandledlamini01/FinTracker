with open('app_chunk.js') as f:
    text = f.read()

import re

# Look for initial transactions array or objects
print("=== SEARCH FOR INITIAL OBJECTS ===")
for m in re.finditer(r'\[\s*\{\s*(?:id|title|text|name)[^\]]+\]', text):
    print("Match:", m.group(0)[:300])

# Look for user defaults
for m in re.finditer(r'\{[^{}]*(?:name|email|Alex|John)[^{}]*\}', text):
    print("User match:", m.group(0)[:200])
