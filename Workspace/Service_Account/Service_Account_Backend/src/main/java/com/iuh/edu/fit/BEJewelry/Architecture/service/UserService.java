package com.iuh.edu.fit.BEJewelry.Architecture.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.Role;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.Meta;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResCreateUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResUpdateUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResultPaginationDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.RoleRepository;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository, 
            RoleService roleService, 
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Get a Role by its name
     * @param roleName the name of the role to find
     * @return the Role object if found, null otherwise
     */
    public Role getRoleByName(String roleName) {
        return this.roleService.getRoleByName(roleName);
    }

    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        return userOptional.orElse(null);
    }

    public User handleGetUserByUserName(String username) {
        return this.userRepository.findByEmail(username);
    }

    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    public List<User> fetchAllUser() {
        return this.userRepository.findAll();
    }

    public User handleCreateUser(User user) {
        if (user.getRole() == null) {
            Role userRole = this.roleRepository.findByName("USER");
            user.setRole(userRole);
        } else {
            Role r = this.roleService.fetchById(user.getRole().getId());
            user.setRole(r != null ? r : null);
        }
        return userRepository.save(user);
    }

    public User handleUpdateUser(User reqUser) {
        User currentUser = this.fetchUserById(reqUser.getId());
        if (currentUser != null) {
            currentUser.setAddress(reqUser.getAddress());
            currentUser.setAge(reqUser.getAge());
            currentUser.setGender(reqUser.getGender());
            currentUser.setName(reqUser.getName());

            if (reqUser.getRole() != null) {
                Role r = this.roleService.fetchById(reqUser.getRole().getId());
                currentUser.setRole(r);
            }

            return this.userRepository.save(currentUser);
        }
        return null;
    }

    public void handleDeleteUser(long id) {
        this.userRepository.deleteById(id);
    }

    // Token management
    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUserName(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    // DTO conversion methods
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        return new ResCreateUserDTO(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getGender(),
                user.getAge(),
                user.getAddress(),
                user.getCreatedAt());
    }

    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        return new ResUpdateUserDTO(
                user.getId(),
                user.getName(),
                user.getGender(),
                user.getAge(),
                user.getAddress(),
                user.getUpdatedAt());
    }

    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        res.setAvatar(user.getAvatar());

        if (user.getRole() != null) {
            ResUserDTO.RoleUser roleUser = new ResUserDTO.RoleUser(
                    user.getRole().getId(),
                    user.getRole().getName());
            res.setRole(roleUser);
        }

        if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
            String avatarUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/")
                    .path(user.getAvatar())
                    .toUriString();
            res.setAvatarUrl(avatarUrl);
        }

        return res;
    }

    public ResultPaginationDTO fetchAllUser(Specification<User> spec, Pageable pageable) {
        Page<User> pageUser = this.userRepository.findAll(spec, pageable);

        List<ResUserDTO> listUser = pageUser.getContent()
                .stream()
                .map(this::convertToResUserDTO)
                .collect(Collectors.toList());

        Meta meta = new com.iuh.edu.fit.BEJewelry.Architecture.domain.response.Meta(
                pageable.getPageNumber() + 1,
                pageable.getPageSize(),
                pageUser.getTotalPages(),
                pageUser.getTotalElements());

        ResultPaginationDTO result = new ResultPaginationDTO();
        result.setMeta(meta);
        result.setResult(listUser);

        return result;
    }
    
    // Add this method to handle user creation with raw password
    public User handleCreateUserWithRawPassword(User user) {
        // Encode the password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return this.handleCreateUser(user);
    }
}