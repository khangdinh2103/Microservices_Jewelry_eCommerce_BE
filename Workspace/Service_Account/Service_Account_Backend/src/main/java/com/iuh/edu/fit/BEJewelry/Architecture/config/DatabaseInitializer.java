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

        // Thêm permissions mới ngay cả khi đã có dữ liệu
        if (true) {  // Thay đổi điều kiện để luôn chạy
            // Kiểm tra xem permission đã tồn tại chưa
            boolean hasOccasionPermissions = permissionRepository.findAll().stream()
                .anyMatch(p -> p.getModule().equals("OCCASIONS"));
            
            if (!hasOccasionPermissions) {
                ArrayList<Permission> occasionPermissions = new ArrayList<>();
                
                // Add occasion permissions
                occasionPermissions.add(new Permission("Get all occasion reminders", "/api/v1/profile/occasions", "GET", "OCCASIONS"));
                occasionPermissions.add(new Permission("Get upcoming occasions", "/api/v1/profile/occasions/upcoming", "GET", "OCCASIONS"));
                occasionPermissions.add(new Permission("Create occasion reminder", "/api/v1/profile/occasions", "POST", "OCCASIONS"));
                occasionPermissions.add(new Permission("Update occasion reminder", "/api/v1/profile/occasions/{id}", "PUT", "OCCASIONS"));
                occasionPermissions.add(new Permission("Delete occasion reminder", "/api/v1/profile/occasions/{id}", "DELETE", "OCCASIONS"));
                
                this.permissionRepository.saveAll(occasionPermissions);
                System.out.println(">>> ADDED OCCASION PERMISSIONS");
                
                // Thêm permissions mới vào SUPER_ADMIN role
                Role adminRole = this.roleRepository.findByName("SUPER_ADMIN");
                if (adminRole != null) {
                    List<Permission> allPermissions = this.permissionRepository.findAll();
                    adminRole.setPermissions(allPermissions);
                    this.roleRepository.save(adminRole);
                }
                
                // Tạo hoặc cập nhật NORMAL_USER role với permissions mới
                Role normalUserRole = this.roleRepository.findByName("NORMAL_USER");
                if (normalUserRole == null) {
                    normalUserRole = new Role();
                    normalUserRole.setName("NORMAL_USER");
                    normalUserRole.setDescription("Người dùng thông thường");
                    normalUserRole.setActive(true);
                }
                
                // Get permissions for normal users
                List<Permission> normalUserPermissions = new ArrayList<>();
                List<Permission> allPermissions = this.permissionRepository.findAll();
                
                for (Permission permission : allPermissions) {
                    if (permission.getModule().equals("OCCASIONS")) {
                        normalUserPermissions.add(permission);
                    } else if (permission.getModule().equals("USERS")) {
                        if (permission.getApiPath().equals("/api/v1/profile") || 
                            permission.getApiPath().equals("/api/v1/profile/change-password")) {
                            normalUserPermissions.add(permission);
                        }
                    }
                }
                
                normalUserRole.setPermissions(normalUserPermissions);
                this.roleRepository.save(normalUserRole);
            }
        }

        // Phần còn lại của code giữ nguyên
        if (countPermissions == 0) {
            ArrayList<Permission> arr = new ArrayList<>();

            arr.add(new Permission("Create a permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
            arr.add(new Permission("Update a permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
            arr.add(new Permission("Delete a permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
            arr.add(new Permission("Get a permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
            arr.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

            arr.add(new Permission("Create a role", "/api/v1/roles", "POST", "ROLES"));
            arr.add(new Permission("Update a role", "/api/v1/roles", "PUT", "ROLES"));
            arr.add(new Permission("Delete a role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
            arr.add(new Permission("Get a role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
            arr.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

            arr.add(new Permission("Create a user", "/api/v1/users", "POST", "USERS"));
            arr.add(new Permission("Update a user", "/api/v1/users", "PUT", "USERS"));
            arr.add(new Permission("Delete a user", "/api/v1/users/{id}", "DELETE", "USERS"));
            arr.add(new Permission("Get a user by id", "/api/v1/users/{id}", "GET", "USERS"));
            arr.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));
            arr.add(new Permission("Get profile user", "/api/v1/profile", "GET", "USERS"));
            arr.add(new Permission("Update profile user", "/api/v1/profile", "PUT", "USERS"));
            arr.add(new Permission("Change password", "/api/v1/profile/change-password", "PUT", "USERS"));

            // Add occasion permissions
            arr.add(new Permission("Get all occasion reminders", "/api/v1/profile/occasions", "GET", "OCCASIONS"));
            arr.add(new Permission("Get upcoming occasions", "/api/v1/profile/occasions/upcoming", "GET", "OCCASIONS"));
            arr.add(new Permission("Create occasion reminder", "/api/v1/profile/occasions", "POST", "OCCASIONS"));
            arr.add(new Permission("Update occasion reminder", "/api/v1/profile/occasions/{id}", "PUT", "OCCASIONS"));
            arr.add(new Permission("Delete occasion reminder", "/api/v1/profile/occasions/{id}", "DELETE", "OCCASIONS"));

            arr.add(new Permission("Download a file", "/api/v1/files", "POST", "FILES"));
            arr.add(new Permission("Upload a file", "/api/v1/files", "GET", "FILES"));

            this.permissionRepository.saveAll(arr);
        }

        if (countRoles == 0) {
            List<Permission> allPermissions = this.permissionRepository.findAll();

            // Create SUPER_ADMIN role with all permissions
            Role adminRole = new Role();
            adminRole.setName("SUPER_ADMIN");
            adminRole.setDescription("Admin thì full permissions");
            adminRole.setActive(true);
            adminRole.setPermissions(allPermissions);
            this.roleRepository.save(adminRole);

            // Create NORMAL_USER role with limited permissions
            Role normalUserRole = new Role();
            normalUserRole.setName("NORMAL_USER");
            normalUserRole.setDescription("Người dùng thông thường");
            normalUserRole.setActive(true);
            
            // Get only the permissions needed for normal users
            List<Permission> normalUserPermissions = new ArrayList<>();
            
            // Add all OCCASIONS permissions
            for (Permission permission : allPermissions) {
                if (permission.getGroup().equals("OCCASIONS")) {
                    normalUserPermissions.add(permission);
                } else if (permission.getGroup().equals("USERS")) {
                    // Add specific user profile permissions
                    if (permission.getEndpoint().equals("/api/v1/profile") || 
                        permission.getEndpoint().equals("/api/v1/profile/change-password")) {
                        normalUserPermissions.add(permission);
                    }
                }
            }
            
            normalUserRole.setPermissions(normalUserPermissions);
            this.roleRepository.save(normalUserRole);
        }

        if (countUsers == 0) {
            // Create admin user
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

            // Create normal user
            User normalUser = new User();
            normalUser.setEmail("user@gmail.com");
            normalUser.setAddress("Hà Nội");
            normalUser.setAge(25);
            normalUser.setGender(GenderEnum.FEMALE);
            normalUser.setName("Normal User");
            normalUser.setPassword(this.passwordEncoder.encode("123456"));

            Role normalUserRole = this.roleRepository.findByName("NORMAL_USER");
            if (normalUserRole != null) {
                normalUser.setRole(normalUserRole);
            }
            this.userRepository.save(normalUser);
        }

        if (countPermissions > 0 && countRoles > 0 && countUsers > 0) {
            System.out.println(">>> SKIP INIT DATABASE ~ ALREADY HAVE DATA...");
        } else
            System.out.println(">>> END INIT DATABASE");
    }

}
