import os
import re

LAYOUT_DIR = r"c:\Users\chait\AndroidStudioProjects\OvaDrugX\app\src\main\res\layout"

for filename in os.listdir(LAYOUT_DIR):
    if (filename.startswith("item_") or filename.startswith("dialog_")) and filename.endswith(".xml"):
        filepath = os.path.join(LAYOUT_DIR, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # We know we added android:background="@color/premium_light_bg" or it was there.
        # Let's remove it if it was added.
        new_content = re.sub(r'\n\s*android:background=\"@color/premium_light_bg\"', '', content, count=1)
        if new_content != content:
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(new_content)
            print(f"Reverted {filename}")
print("Done fixing items.")
