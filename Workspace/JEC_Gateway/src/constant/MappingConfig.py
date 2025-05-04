import json

from constant.PathConfig import Path


class MappingConfig:
    _MAPPING_BE_FILE_PATH = f"{Path.rsc}/service/mapping_be.json"
    _MAPPING_FE_FILE_PATH = f"{Path.rsc}/service/mapping_fe.json"

    @classmethod
    def read_mapping_be_json(cls) -> dict:
        with open(cls._MAPPING_BE_FILE_PATH, "r") as file:
            return json.load(file)

    @classmethod
    def read_mapping_fe_json(cls) -> dict:
        with open(cls._MAPPING_FE_FILE_PATH, "r") as file:
            return json.load(file)
