import os
import re

LAYOUT_DIR = r"c:\Users\chait\AndroidStudioProjects\OvaDrugX\app\src\main\res\layout"

def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    # Regex to match the first opening tag (which is the root element)
    # The regex looks for `<` followed by tag name (not xml or !--), then any attributes until `>`
    match = re.search(r'(<(?!(?:xml|!--|\?xml))[a-zA-Z0-9_\.]+\s+)([^>]*?)(/?>)', content)
    
    if not match:
        print(f"Skipping {os.path.basename(filepath)} - No root tag found")
        return

    start_tag = match.group(1)
    attrs = match.group(2)
    end_tag = match.group(3)
    
    # Check if there is an existing background attribute
    if 'android:background=' in attrs:
        # replace it
        new_attrs = re.sub(r'android:background="[^"]+"', 'android:background="@color/premium_light_bg"', attrs)
    else:
        # add it
        new_attrs = attrs + '\n    android:background="@color/premium_light_bg"'
        
    # Reconstruct the document
    if new_attrs != attrs:
        new_content = content[:match.start()] + start_tag + new_attrs + end_tag + content[match.end():]
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        print(f"Modified {os.path.basename(filepath)}")

for filename in os.listdir(LAYOUT_DIR):
    if filename.endswith(".xml"):
        process_file(os.path.join(LAYOUT_DIR, filename))

print("Done.")
