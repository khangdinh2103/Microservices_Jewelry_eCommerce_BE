package com.iuh.edu.fit.BEJewelry.Architecture.util;

import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.RestResponse;
import com.iuh.edu.fit.BEJewelry.Architecture.util.annotation.ApiMessage;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class FormatRestResponse implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        if (body instanceof String || body instanceof Resource) {
            return body;
        }

        HttpServletResponse servletResponse = ((ServletServerHttpResponse) response).getServletResponse();
        int status = servletResponse.getStatus();

        if (status >= 400) {
            return body;
        }

        RestResponse<Object> res = new RestResponse<>();
        res.setStatusCode(status);
        res.setData(body);

        ApiMessage message = returnType.getMethodAnnotation(ApiMessage.class);
        res.setMessage(message != null ? message.value() : "CALL API SUCCESS");

        return res;
    }
}