import xml.etree.ElementTree as ET
import os

layout_dir = r"c:\Users\chait\AndroidStudioProjects\OvaDrugX\app\src\main\res\layout"
for filename in os.listdir(layout_dir):
    if filename.endswith(".xml"):
        path = os.path.join(layout_dir, filename)
        try:
            ET.parse(path)
        except ET.ParseError as e:
            print(f"Error in {filename}: {e}")
        except Exception as e:
            print(f"Failed to read {filename}: {e}")
print("Validation complete.")
