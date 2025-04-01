import json


class FileManager:
    @staticmethod
    def read_json_object(file_path: str) -> dict:
        with open(file_path, "r") as file:
            return json.load(file)
