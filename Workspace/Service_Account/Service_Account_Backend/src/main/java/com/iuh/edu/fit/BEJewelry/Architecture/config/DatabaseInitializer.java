package com.iuh.edu.fit.BEJewelry.Architecture.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.Permission;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.Role;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.PermissionRepository;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.RoleRepository;
import com.iuh.edu.fit.BEJewelry.Architecture.repository.UserRepository;
import com.iuh.edu.fit.BEJewelry.Architecture.util.constant.GenderEnum;

@Service
public class DatabaseInitializer implements CommandLineRunner {
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DatabaseInitializer(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println(">>> START INIT DATABASE");

        long countPermissions = this.permissionRepository.count();
        long countRoles = this.roleRepository.count();
        long countUsers = this.userRepository.count();

        if (countPermissions == 0) {
            initializePermissions();
        }

        if (countRoles == 0) {
            initializeRoles();
        }

        if (countUsers == 0) {
            initializeUsers();
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else {
            System.out.println(">>> END INIT DATABASE");
        }
    }

    private void initializePermissions() {
        ArrayList<Permission> permissions = new ArrayList<>();

        // Permission management
        permissions.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
        permissions.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
        permissions.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
        permissions.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
        permissions.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

        // Role management
        permissions.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
        permissions.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
        permissions.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
        permissions.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
        permissions.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

        // User management
        permissions.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
        permissions.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
        permissions.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
        permissions.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
        permissions.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));
        permissions.add(new Permission("Get profile user", "/api/v1/profile", "GET", "USERS"));
        permissions.add(new Permission("Update profile user", "/api/v1/profile", "PUT", "USERS"));
        permissions.add(new Permission("Change password", "/api/v1/profile/change-password", "PUT", "USERS"));

        // File operations
        permissions.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
        permissions.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));
        permissions.add(new Permission("Upload avatar", "/api/v1/profile/avatar", "POST", "USERS"));
        permissions.add(new Permission("Get file", "/api/v1/files/{fileName}", "GET", "FILES"));
        permissions.add(new Permission("Upload file", "/api/v1/files/upload", "POST", "FILES"));

        this.permissionRepository.saveAll(permissions);
    }

    private void initializeRoles() {
        List<Permission> allPermissions = this.permissionRepository.findAll();

        Role adminRole = new Role();
        adminRole.setName("SUPER_ADMIN");
        adminRole.setDescription("Admin thì full permissions");
        adminRole.setActive(true);
        adminRole.setPermissions(allPermissions);

        this.roleRepository.save(adminRole);
    }

    private void initializeUsers() {
        User adminUser = new User();
        adminUser.setEmail("admin@gmail.com");
        adminUser.setAddress("Sài Gòn");
        adminUser.setAge(22);
        adminUser.setGender(GenderEnum.MALE);
        adminUser.setName("I'm super admin");
        adminUser.setPassword(this.passwordEncoder.encode("123456"));

        Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
        if (adminRole != null) {
            adminUser.setRole(adminRole);
        }

        this.userRepository.save(adminUser);
    }
}