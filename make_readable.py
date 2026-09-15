with open('original_screens.js') as f:
    text = f.read()

import re

# Split by sections or components
# Replace minified JSX with readable lines
readable = text
readable = readable.replace('(0,t.jsx)', '\nJSX: ')
readable = readable.replace('(0,t.jsxs)', '\nJSXS: ')
readable = readable.replace('className:', '\n  class: ')
readable = readable.replace('children:', '\n  children: ')

with open('readable_screens.txt', 'w') as f:
    f.write(readable)

print("Wrote readable_screens.txt")
