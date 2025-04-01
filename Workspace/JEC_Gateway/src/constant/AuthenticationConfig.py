from constant.PathConfig import Path


class AuthenticationConfig:
    _SECRET_KEY_FILE_PATH = f"{Path.rsc}/authentication/secret.key"
    _ALGORITHMS = ["HS256"]

    @classmethod
    def read_secret_key(cls):
        with open(cls._SECRET_KEY_FILE_PATH, "r") as f:
            return f.read().strip()

    @classmethod
    def get_algorithms(cls):
        return cls._ALGORITHMS
