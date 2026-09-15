with open('app_chunk.js') as f:
    text = f.read()

import re

# Let's find all object structures, arrays, or JSX components
print("=== TEXT SNIPPETS ===")
matches = re.findall(r'["\']([^"\']{4,60})["\']', text)
seen = set()
for m in matches:
    low = m.lower()
    if any(k in low for k in ['budget', 'wallet', 'finance', 'saving', 'goal', 'expense', 'income', 'balance', 'card', 'transaction', 'category', 'analytics', 'dashboard', 'overview', 'recent', 'home', 'chart', 'activity', 'profile', 'settings', 'add', 'monthly']):
        if m not in seen and not m.startswith('http') and not '/' in m:
            seen.add(m)
            print(m)
