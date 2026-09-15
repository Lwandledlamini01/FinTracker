with open('app_chunk.js') as f:
    text = f.read()

# Let's find each screen component:
# Dashboard, Wallet, Goals, Profile, AddModal, SettingsSidebar
screens = ["Dashboard", "Wallet", "Savings Goals", "Account Profile", "Pending Tasks", "Available Balance"]

for s in screens:
    pos = text.find(s)
    print(f"=== {s} at {pos} ===")
    if pos != -1:
        print(text[pos-100:pos+1200])
