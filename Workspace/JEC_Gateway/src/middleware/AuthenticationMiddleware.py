import logging

import jwt
from fastapi import HTTPException
from starlette.requests import Request
from starlette.responses import JSONResponse

from constant.AuthenticationConfig import AuthenticationConfig


class AuthenticationMiddleware:
    @staticmethod
    async def authenticate(request: Request, call_next):
        try:
            if request.url.path.startswith("/"):
                path_parts = request.url.path.strip("/").split("/")
                if path_parts and path_parts[0] == "api":
                    path_parts.pop(0)
                service = path_parts[0] if path_parts else ""
                if service == "account":
                    return await call_next(request)

                authorization = request.headers.get("Authorization")
                if not authorization:
                    return JSONResponse(status_code=401, content={"detail": "Authorization header missing"})

                try:
                    auth_type, token = authorization.split(" ", 1)
                    if auth_type.lower() != "bearer":
                        return JSONResponse(status_code=401, content={"detail": "Invalid authorization type. Bearer token required"})
                except ValueError:
                    return JSONResponse(status_code=401, content={"detail": "Invalid Authorization header format"})

                try:
                    payload = AuthenticationMiddleware.verify_token(token)
                    request.state.user = payload
                except HTTPException as he:
                    return JSONResponse(status_code=he.status_code, content={"detail": he.detail})
            return await call_next(request)

        except Exception as e:
            logging.error(f"Authentication error: {str(e)}")

            return JSONResponse(status_code=500, content={"detail": "Internal server error during authentication"})

    @classmethod
    def verify_token(cls, token: str):
        try:
            payload = jwt.decode(token, AuthenticationConfig.read_secret_key(), algorithms=AuthenticationConfig.get_algorithms())
            return payload
        except jwt.ExpiredSignatureError:
            raise HTTPException(status_code=401, detail="Token expired")
        except jwt.PyJWTError:
            raise HTTPException(status_code=401, detail="Invalid token")
        except Exception as e:
            raise HTTPException(status_code=401, detail=f"Token verification failed: {e}")
