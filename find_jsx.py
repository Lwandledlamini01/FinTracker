with open('app_chunk.js') as f:
    text = f.read()

import re

# Let's find all text literals inside JSX
matches = re.findall(r'children:\s*(\[[^\]]+\]|"[^"]+"|\'[^\']+\')', text)
print(f"Found {len(matches)} children expressions")

# Let's search for SVG icons or classes
classes = re.findall(r'className:\s*["`]([^"`]+)["`]', text)
print(f"Found {len(classes)} classNames")
for c in classes[:30]:
    print("Class:", c)
