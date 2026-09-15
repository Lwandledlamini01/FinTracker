with open('app_chunk.js') as f:
    text = f.read()

import re

# Split by component definitions
# Let's find all chunks that return JSX
print("=== TEXT SNIPPETS AND HEADINGS ===")
for m in re.finditer(r'<[a-zA-Z0-9]+[^>]*>|["\']([A-Z][a-zA-Z0-9\s\$\,\.\!\?\:\-]{2,40})["\']', text):
    g = m.group(1)
    if g and not g.startswith("Next") and not g.startswith("BAILOUT"):
        print(g)
