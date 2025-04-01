import httpx
from fastapi import HTTPException
from starlette.requests import Request
from starlette.responses import JSONResponse


class Proxy:
    @staticmethod
    async def proxy_request(request: Request, route: dict, service: str, action: str):
        if service not in route:
            raise HTTPException(status_code=404, detail=f"Service {service} not found")

        service_info = route[service]
        actions = service_info.get("actions", {})

        if not action or action not in actions:
            raise HTTPException(status_code=404, detail=f"Action {action} not found for service {service}")

        allowed_roles = actions[action]
        user_role = request.state.user.get("role")

        if user_role not in allowed_roles:
            raise HTTPException(status_code=403, detail="You do not have permission to access this resource")

        async with httpx.AsyncClient() as client:
            host = service_info["host"]
            port = service_info["port"]
            route = service_info["route"]
            if not route or route.startswith("/"):
                pass
            else:
                raise HTTPException(status_code=500, detail="Invalid route")
            url = f"http://{host}:{port}{route}/{action}"
            print(f"URL: {url}")
            headers = request.headers
            method = request.method.lower()

            if method == "get":
                response = await client.get(url, headers=headers)
            else:
                response = await client.request(method, url, headers=headers, content=await request.body())

        return JSONResponse(content=response.json(), status_code=response.status_code, headers=dict(response.headers))
