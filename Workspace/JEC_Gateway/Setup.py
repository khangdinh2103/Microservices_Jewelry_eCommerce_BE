from setuptools import setup

setup(
    install_requires=[
        # Web framework
        "fastapi",
        "starlette",

        # ASGI server
        "uvicorn",

        # JWT libraries
        "jwt",
        "PyJWT",

        # HTTP clients
        "requests",
        "httpx",

        # Data validation
        "pydantic",

        # Miscellaneous
        "python-dotenv",
    ]
)
