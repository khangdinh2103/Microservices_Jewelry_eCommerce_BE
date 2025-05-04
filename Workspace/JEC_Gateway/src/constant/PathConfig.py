import os

class Path:
    dir = os.path.dirname(os.path.abspath(__file__))
    src = os.path.dirname(dir)
    root = os.path.dirname(src)
    rsc = os.path.join(root, "rsc")
    rsc_service_mapping = os.path.join(rsc, "service", "mapping.json")
