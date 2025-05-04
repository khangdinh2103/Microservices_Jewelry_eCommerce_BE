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

        if action not in actions:
            raise HTTPException(status_code=404, detail=f"Action {action} not found for service {service}")

        allowed_roles = actions.get(action, [])
        user_role = getattr(request.state, "user", {}).get("role")

        if allowed_roles and user_role not in allowed_roles:
            raise HTTPException(status_code=403, detail="You do not have permission to access this resource")

        host = service_info["host"]
        port = service_info["port"]
        service_route = service_info.get("route", "")

        if service_route and not service_route.startswith("/"):
            raise HTTPException(status_code=500, detail="Invalid route configuration")

        url = f"http://{host}:{port}{service_route}/{action}"
        headers = dict(request.headers)
        method = request.method.lower()
        timeout = httpx.Timeout(10.0, connect=5.0)

        try:
            async with httpx.AsyncClient(timeout=timeout) as client:
                if method == "get":
                    response = await client.get(url, headers=headers, params=dict(request.query_params))
                elif method == "post":
                    body = await request.body()
                    response = await client.post(url, headers=headers, content=body)
                elif method == "put":
                    body = await request.body()
                    response = await client.put(url, headers=headers, content=body)
                elif method == "delete":
                    response = await client.delete(url, headers=headers)
                else:
                    raise HTTPException(status_code=405, detail=f"Method {method} not supported")

                return JSONResponse(
                    content=response.json(),
                    status_code=response.status_code,
                    headers=dict(response.headers)
                )

        except httpx.TimeoutException:
            return JSONResponse(status_code=504, content={"detail": f"Service {service} is not responding"})

        except httpx.ConnectError:
            return JSONResponse(status_code=503, content={"detail": f"Service {service} is currently unavailable"})

        except httpx.HTTPStatusError as e:
            try:
                error_content = e.response.json()
            except Exception as ex:
                error_content = {"detail": f"{str(e)} - Reason: {str(ex)}"}

            return JSONResponse(status_code=e.response.status_code, content=error_content)

        except Exception as e:
            return JSONResponse(status_code=500, content={"detail": f"Internal server error occurred while connecting to service - {str(e)}"})
