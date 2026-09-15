with open('app_chunk.js') as f:
    text = f.read()

import re

# Find initial state or default objects in text
print("=== INITIAL DATA / CONSTANTS ===")
for chunk in text.split("financeflow_"):
    print("--- CHUNK ---")
    print(chunk[:300])

