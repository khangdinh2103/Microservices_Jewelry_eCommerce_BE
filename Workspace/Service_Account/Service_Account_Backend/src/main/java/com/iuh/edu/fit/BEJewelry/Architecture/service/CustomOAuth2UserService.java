package com.iuh.edu.fit.BEJewelry.Architecture.service;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.Role;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserService userService;

    @Autowired
    @Lazy
    private SecurityUtil securityUtil;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        // Extract information from Google
        Map<String, Object> attributes = oauth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // Check if user already exists
        User existingUser = userService.handleGetUserByUserName(email);

        if (existingUser == null) {
            // Create new user
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);

            // Generate a random password using SecurityUtil and encode it
            String randomPassword = securityUtil.generateRandomPassword();
            BCryptPasswordEncoder localEncoder = new BCryptPasswordEncoder();
            newUser.setPassword(localEncoder.encode(randomPassword));

            // Get the default role
            Role userRole = userService.getRoleByName("USER");
            if (userRole != null) {
                newUser.setRole(userRole);
            }

            userService.handleCreateUser(newUser);
        }

        return oauth2User;
    }
}