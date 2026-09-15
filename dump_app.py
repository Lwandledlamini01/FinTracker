import jsbeautifier
with open('app_chunk.js') as f:
    text = f.read()

# Let's find where FinanceApp is defined and dump the code
idx = text.find('FinanceApp')
snippet = text[idx-50:idx+25000]
print("Found FinanceApp at", idx)
with open('finance_app.js', 'w') as out:
    out.write(snippet)
print("Wrote finance_app.js")
