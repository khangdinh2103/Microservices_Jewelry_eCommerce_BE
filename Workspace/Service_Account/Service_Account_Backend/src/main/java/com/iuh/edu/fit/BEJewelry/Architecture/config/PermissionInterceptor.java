package com.iuh.edu.fit.BEJewelry.Architecture.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.Permission;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.Role;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.service.UserService;
import com.iuh.edu.fit.BEJewelry.Architecture.util.SecurityUtil;
import com.iuh.edu.fit.BEJewelry.Architecture.util.error.PermissionException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PermissionInterceptor implements HandlerInterceptor {
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String path = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String httpMethod = request.getMethod();

        System.out.println(">>> RUN preHandle");
        System.out.println(">>> path= " + path);
        System.out.println(">>> httpMethod= " + httpMethod);
        System.out.println(">>> requestURI= " + request.getRequestURI());

        // Check if user is authenticated
        String email = SecurityUtil.getCurrentUserLogin().orElse("");
        if (email.isEmpty()) {
            return true;
        }

        // Verify user permissions
        User user = this.userService.handleGetUserByUserName(email);
        if (user == null) {
            return true;
        }

        Role role = user.getRole();
        if (role == null) {
            throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
        }

        List<Permission> permissions = role.getPermissions();
        boolean isAllowed = permissions.stream()
                .anyMatch(item -> item.getApiPath().equals(path) && item.getMethod().equals(httpMethod));

        if (!isAllowed) {
            throw new PermissionException("Bạn không có quyền truy cập endpoint này.");
        }

        return true;
    }
}