import uvicorn
from fastapi import FastAPI
from starlette.requests import Request

from constant.MappingConfig import MappingConfig
from middleware.AuthenticationMiddleware import AuthenticationMiddleware
from proxy.Proxy import Proxy

_app = FastAPI()

_app.middleware("http")(AuthenticationMiddleware.authenticate)

@_app.api_route("/api/{service}/{action}", methods=["GET", "POST", "PUT", "DELETE"])
async def proxy_backend(request: Request, service: str, action: str):
    mapping = MappingConfig.read_mapping_be_json()
    return await Proxy.proxy_request(request, mapping, service, action)

@_app.api_route("/{service}/{action}", methods=["GET", "POST", "PUT", "DELETE"])
async def proxy_frontend(request: Request, service: str, action: str):
    mapping = MappingConfig.read_mapping_fe_json()
    return await Proxy.proxy_request(request, mapping, service, action)

if __name__ == "__main__":
    uvicorn.run(_app, host="0.0.0.0", port=8000)
