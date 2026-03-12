import xml.etree.ElementTree as ET
import os

res_dir = r"c:\Users\chait\AndroidStudioProjects\OvaDrugX\app\src\main\res\values"
files_to_check = ["dimens.xml", "themes.xml"]
for filename in files_to_check:
    path = os.path.join(res_dir, filename)
    if os.path.exists(path):
        try:
            ET.parse(path)
            print(f"{filename} is valid.")
        except ET.ParseError as e:
            print(f"Error in {filename}: {e}")
        except Exception as e:
            print(f"Failed to read {filename}: {e}")
    else:
        print(f"{filename} NOT FOUND at {path}")
print("Validation complete.")
