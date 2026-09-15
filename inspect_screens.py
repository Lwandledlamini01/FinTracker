with open('original_screens.js') as f:
    text = f.read()

import re

# Find JSX blocks or component patterns
print("Length:", len(text))
# Let's search for headers, cards, sections
for m in re.finditer(r'<section|<div className="[^"]*(?:card|header|balance|task|goal|nav|bottom)[^"]*"', text):
    print("Pattern:", m.group(0))

# Let's print out the structure
matches = re.findall(r'children:\[\s*(?:\(0,t\.jsx[s]?\)\([^)]+\)[,\s]*)+\]', text)
print(f"Found {len(matches)} compound children")
