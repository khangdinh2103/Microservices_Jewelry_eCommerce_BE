package com.iuh.edu.fit.BEJewelry.Architecture.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

import jakarta.validation.Valid;

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

    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, SecurityUtil securityUtil,
            UserService userService, PasswordEncoder passwordEncoder, AuthService authService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.securityUtil = securityUtil;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
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

        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @PostMapping("/auth/forgot-password")
    public ResponseEntity<RestResponse<String>> forgotPassword(@Valid @RequestBody ReqForgotPasswordDTO request) {
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
}