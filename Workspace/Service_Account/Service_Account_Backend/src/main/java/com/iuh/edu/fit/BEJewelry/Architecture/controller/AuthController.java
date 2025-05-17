package com.iuh.edu.fit.BEJewelry.Architecture.controller;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.Role;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.request.ReqForgotPasswordDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.request.ReqLoginDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.request.ReqResetPasswordDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResCreateUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResLoginDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.RestResponse;
import com.iuh.edu.fit.BEJewelry.Architecture.service.AuthService;
import com.iuh.edu.fit.BEJewelry.Architecture.service.UserService;
import com.iuh.edu.fit.BEJewelry.Architecture.util.SecurityUtil;
import com.iuh.edu.fit.BEJewelry.Architecture.util.annotation.ApiMessage;
import com.iuh.edu.fit.BEJewelry.Architecture.util.error.IdInvalidException;
import com.iuh.edu.fit.BEJewelry.Architecture.util.error.PermissionException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final SecurityUtil securityUtil;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    @Value("${jec.jwt.refresh-token-validity-in-seconds}")
    private long refreshTokenExpiration;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
                          UserService userService, PasswordEncoder passwordEncoder, AuthService authService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    @GetMapping("/auth/google")
    @ApiMessage("Handle Google Login")
    public ResponseEntity<?> handleGoogleLogin(@AuthenticationPrincipal OAuth2User principal,
                                               HttpServletRequest request) {
        // Add debug logging
        System.out.println("Google login endpoint called");

        if (principal == null) {
            System.out.println("OAuth2User principal is null");
            ResLoginDTO response = new ResLoginDTO();
            response.setError("Vui lòng đăng nhập với Google");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        // Rest of the method remains the same
        System.out.println("Principal: Not null");
        System.out.println("Principal attributes: " + principal.getAttributes());

        String email = principal.getAttribute("email");
        if (email == null) {
            System.out.println("Email attribute is missing from OAuth2User");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResLoginDTO("Không thể lấy email từ tài khoản Google"));
        }

        System.out.println("Email from Google: " + email);

        // Find user in database
        User currentUserDB = this.userService.handleGetUserByUserName(email);

        // If user doesn't exist, create a new one
        if (currentUserDB == null) {
            try {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(principal.getAttribute("name"));
                // Generate a random password since we're using OAuth
                String randomPassword = this.securityUtil.generateRandomPassword();
                newUser.setPassword(this.passwordEncoder.encode(randomPassword));

                // You need to get the Role object from your service or repository
                Role userRole = this.userService.getRoleByName("NORMAL_USER");
                if (userRole == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ResLoginDTO("Không tìm thấy vai trò NORMAL_USER"));
                }
                newUser.setRole(userRole);

                currentUserDB = this.userService.handleCreateUser(newUser);

                if (currentUserDB == null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(new ResLoginDTO("Không thể tạo người dùng mới"));
                }
            } catch (Exception e) {
                System.out.println("Error creating new user: " + e.getMessage());
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResLoginDTO("Lỗi khi tạo người dùng mới: " + e.getMessage()));
            }
        }

        // Create response
        ResLoginDTO res = new ResLoginDTO();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                currentUserDB.getId(),
                currentUserDB.getEmail(),
                currentUserDB.getName(),
                currentUserDB.getRole());
        res.setUser(userLogin);

        // Create access token
        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        // Create refresh token
        String refreshToken = this.securityUtil.createRefreshToken(email, res);

        // Update token for user
        this.userService.updateUserToken(refreshToken, email);

        // Set cookie
        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        System.out.println("Google authentication successful for: " + email);

        // Check if this is an AJAX request or direct browser navigation
        String acceptHeader = request.getHeader("Accept");
        boolean isAjaxRequest = acceptHeader != null && acceptHeader.contains("application/json");

        if (isAjaxRequest) {
            // Return JSON response for AJAX requests
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                    .body(res);
        } else {
            // Redirect to frontend application for browser navigation
            HttpHeaders headers = new HttpHeaders();

            // Create a URL with user information and URL encode the name to handle special
            // characters
            String redirectUrl = frontendUrl +
                    // Add a specific path for handling login success
                    "?token=" + access_token +
                    "&userId=" + currentUserDB.getId() +
                    "&email=" + java.net.URLEncoder.encode(currentUserDB.getEmail(), StandardCharsets.UTF_8) +
                    "&name=" + java.net.URLEncoder.encode(currentUserDB.getName(), StandardCharsets.UTF_8);

            // Add role if available
            if (currentUserDB.getRole() != null) {
                redirectUrl += "&role=" + currentUserDB.getRole().getName();
            }

            System.out.println("Redirecting to: " + redirectUrl);
            headers.add("Location", redirectUrl);

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                    .headers(headers)
                    .build();
        }
    }

    @GetMapping("/auth/debug-oauth")
    public ResponseEntity<Map<String, Object>> debugOAuth(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        // Get session information
        HttpSession session = request.getSession(false);
        if (session != null) {
            response.put("sessionId", session.getId());
            response.put("sessionCreationTime", new Date(session.getCreationTime()));
            response.put("sessionLastAccessedTime", new Date(session.getLastAccessedTime()));

            // Get all session attribute names
            Enumeration<String> attributeNames = session.getAttributeNames();
            Map<String, Object> sessionAttributes = new HashMap<>();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                // Don't include sensitive attributes
                if (!name.contains("SPRING_SECURITY_OAUTH2_AUTHORIZATION_REQUEST")) {
                    sessionAttributes.put(name, session.getAttribute(name));
                } else {
                    sessionAttributes.put(name, "REDACTED FOR SECURITY");
                }
            }
            response.put("sessionAttributes", sessionAttributes);
        } else {
            response.put("session", "No active session");
        }

        // Get cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            Map<String, String> cookieMap = new HashMap<>();
            for (Cookie cookie : cookies) {
                cookieMap.put(cookie.getName(), cookie.getValue());
            }
            response.put("cookies", cookieMap);
        } else {
            response.put("cookies", "No cookies");
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/test-oauth")
    @ApiMessage("Test OAuth Authentication")
    public ResponseEntity<Map<String, Object>> testOAuth(@AuthenticationPrincipal OAuth2User principal) {
        Map<String, Object> response = new HashMap<>();

        if (principal == null) {
            response.put("authenticated", false);
            response.put("message", "Not authenticated with OAuth2");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("authenticated", true);
        response.put("name", principal.getAttribute("name"));
        response.put("email", principal.getAttribute("email"));
        response.put("attributes", principal.getAttributes());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/login")
    public ResponseEntity<ResLoginDTO> login(@Valid @RequestBody ReqLoginDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginDTO.getUsername(), loginDTO.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(loginDTO.getUsername());
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole());
            res.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(authentication.getName(), res);
        res.setAccessToken(access_token);

        String refreshToken = this.securityUtil.createRefreshToken(loginDTO.getUsername(), res);
        this.userService.updateUserToken(refreshToken, loginDTO.getUsername());

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, resCookies.toString()).body(res);
    }

    @GetMapping("/auth/account")
    @ApiMessage("fetch account")
    public ResponseEntity<ResLoginDTO.UserGetAccount> getAccount() {
        String email = SecurityUtil.getCurrentUserLogin().isPresent()
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        User currentUserDB = this.userService.handleGetUserByUserName(email);
        ResLoginDTO.UserGetAccount userGetAccount = new ResLoginDTO.UserGetAccount();
        ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin();

        if (currentUserDB != null) {
            userLogin.setId(currentUserDB.getId());
            userLogin.setEmail(currentUserDB.getEmail());
            userLogin.setName(currentUserDB.getName());
            userLogin.setRole(currentUserDB.getRole());
        }
        userGetAccount.setUser(userLogin);

        return ResponseEntity.ok().body(userGetAccount);
    }

    @GetMapping("/auth/refresh")
    @ApiMessage("Get User by refresh token")
    public ResponseEntity<ResLoginDTO> getRefreshToken(
            @CookieValue(name = "refresh_token", defaultValue = "abc") String refresh_token) throws IdInvalidException {
        if (refresh_token.equals("abc")) {
            throw new IdInvalidException("Bạn không có refresh token ở cookie");
        }

        Jwt decodedToken = this.securityUtil.checkValidRefreshToken(refresh_token);
        String email = decodedToken.getSubject();

        User currentUser = this.userService.getUserByRefreshTokenAndEmail(refresh_token, email);
        if (currentUser == null) {
            throw new IdInvalidException("Refresh Token không hợp lệ");
        }

        ResLoginDTO res = new ResLoginDTO();
        User currentUserDB = this.userService.handleGetUserByUserName(email);
        if (currentUserDB != null) {
            ResLoginDTO.UserLogin userLogin = new ResLoginDTO.UserLogin(
                    currentUserDB.getId(),
                    currentUserDB.getEmail(),
                    currentUserDB.getName(),
                    currentUserDB.getRole());
            res.setUser(userLogin);
        }

        String access_token = this.securityUtil.createAccessToken(email, res);
        res.setAccessToken(access_token);

        String new_refresh_token = this.securityUtil.createRefreshToken(email, res);
        this.userService.updateUserToken(new_refresh_token, email);

        ResponseCookie resCookies = ResponseCookie
                .from("refresh_token", new_refresh_token)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(refreshTokenExpiration)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, resCookies.toString())
                .body(res);
    }

    @PostMapping("/auth/logout")
    @ApiMessage("Logout User")
    public ResponseEntity<Void> logout() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() ? SecurityUtil.getCurrentUserLogin().get() : "";

        if (email.isEmpty()) {
            throw new IdInvalidException("Access Token không hợp lệ");
        }

        this.userService.updateUserToken(null, email);

        ResponseCookie deleteSpringCookie = ResponseCookie
                .from("refresh_token", null)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteSpringCookie.toString())
                .body(null);
    }

    @PostMapping("/auth/register")
    @ApiMessage("Register a new user")
    public ResponseEntity<ResCreateUserDTO> register(@Valid @RequestBody User postManUser) throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException(
                    "Email " + postManUser.getEmail() + " đã tồn tại, vui lòng sử dụng email khác.");
        }

        Role userRole = this.userService.getRoleByName("NORMAL_USER");
        if (userRole == null) {
            throw new IdInvalidException("Không tìm thấy vai trò NORMAL_USER");
        }
        postManUser.setRole(userRole);

        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<RestResponse<String>> forgotPassword(@Valid @RequestBody ReqForgotPasswordDTO request)
            throws PermissionException {
        String result = authService.forgotPassword(request.getEmail());
        RestResponse<String> response = new RestResponse<>(200, null, "Mã xác nhận đã gửi thành công", result);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/auth/reset-password")
    public ResponseEntity<RestResponse<String>> resetPassword(@Valid @RequestBody ReqResetPasswordDTO request)
            throws PermissionException {
        String result = authService.resetPassword(request.getToken(), request.getNewPassword());
        RestResponse<String> response = new RestResponse<>(200, null, "Mật khẩu đã được đặt lại thành công", result);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/google-login-link")
    @ApiMessage("Get Google Login Link")
    public ResponseEntity<Map<String, String>> getGoogleLoginLink() {
        Map<String, String> response = new HashMap<>();
        response.put("login_url", "/oauth2/authorization/google");
        response.put("message", "Truy cập URL này để đăng nhập với Google");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/auth/google-redirect")
    @ApiMessage("Redirect to Google Login")
    public ResponseEntity<Void> redirectToGoogleLogin() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", "/oauth2/authorization/google");
        System.out.println("Redirecting to Google login...");
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/auth/oauth2-config")
    @ApiMessage("Check OAuth2 Configuration")
    public ResponseEntity<Map<String, Object>> checkOAuth2Config() {
        Map<String, Object> response = new HashMap<>();

        try {
            // Add basic info about the application
            response.put("status", "OK");
            response.put("message", "OAuth2 configuration check");
            response.put("timestamp", new java.util.Date().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "ERROR");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
