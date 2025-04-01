import os


class AuthenticationConfig:
    _ALGORITHMS = ["HS256"]

    @classmethod
    def read_secret_key(cls):
        secret_key = os.getenv("SECRET_KEY")
        if not secret_key:
            raise ValueError("SECRET_KEY environment variable is not set")
        return secret_key

    @classmethod
    def get_algorithms(cls):
        return cls._ALGORITHMS
