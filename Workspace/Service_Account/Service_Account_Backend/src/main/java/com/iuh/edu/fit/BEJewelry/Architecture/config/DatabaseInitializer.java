package com.iuh.edu.fit.BEJewelry.Architecture.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<Permission> permissions = new ArrayList<>();

        // Permission management
        permissions.add(new Permission("Create permission", "/api/v1/permissions", "POST", "PERMISSIONS"));
        permissions.add(new Permission("Update permission", "/api/v1/permissions", "PUT", "PERMISSIONS"));
        permissions.add(new Permission("Delete permission", "/api/v1/permissions/{id}", "DELETE", "PERMISSIONS"));
        permissions.add(new Permission("Get permission by id", "/api/v1/permissions/{id}", "GET", "PERMISSIONS"));
        permissions.add(new Permission("Get permissions with pagination", "/api/v1/permissions", "GET", "PERMISSIONS"));

        // Role management
        permissions.add(new Permission("Create role", "/api/v1/roles", "POST", "ROLES"));
        permissions.add(new Permission("Update role", "/api/v1/roles", "PUT", "ROLES"));
        permissions.add(new Permission("Delete role", "/api/v1/roles/{id}", "DELETE", "ROLES"));
        permissions.add(new Permission("Get role by id", "/api/v1/roles/{id}", "GET", "ROLES"));
        permissions.add(new Permission("Get roles with pagination", "/api/v1/roles", "GET", "ROLES"));

        // User management
        permissions.add(new Permission("Create user", "/api/v1/users", "POST", "USERS"));
        permissions.add(new Permission("Update user", "/api/v1/users", "PUT", "USERS"));
        permissions.add(new Permission("Delete user", "/api/v1/users/{id}", "DELETE", "USERS"));
        permissions.add(new Permission("Get user by id", "/api/v1/users/{id}", "GET", "USERS"));
        permissions.add(new Permission("Get users with pagination", "/api/v1/users", "GET", "USERS"));

        // Profile management
        permissions.add(new Permission("Get profile", "/api/v1/profile", "GET", "PROFILES"));
        permissions.add(new Permission("Update profile", "/api/v1/profile", "PUT", "PROFILES"));
        permissions.add(new Permission("Change password", "/api/v1/profile/change-password", "PUT", "PROFILES"));
        permissions.add(new Permission("Upload avatar", "/api/v1/profile/avatar", "POST", "PROFILES"));

        // File operations
        permissions.add(new Permission("Download file", "/api/v1/files/{fileName}", "GET", "FILES"));
        permissions.add(new Permission("Upload file", "/api/v1/files/upload", "POST", "FILES"));

        // Product management
        permissions.add(new Permission("Create product", "/api/v1/products", "POST", "PRODUCTS"));
        permissions.add(new Permission("Update product", "/api/v1/products", "PUT", "PRODUCTS"));
        permissions.add(new Permission("Delete product", "/api/v1/products/{id}", "DELETE", "PRODUCTS"));
        permissions.add(new Permission("Get product by id", "/api/v1/products/{id}", "GET", "PRODUCTS"));
        permissions.add(new Permission("Get products with pagination", "/api/v1/products", "GET", "PRODUCTS"));

        // Order management
        permissions.add(new Permission("Create order", "/api/v1/orders", "POST", "ORDERS"));
        permissions.add(new Permission("Update order", "/api/v1/orders", "PUT", "ORDERS"));
        permissions.add(new Permission("Delete order", "/api/v1/orders/{id}", "DELETE", "ORDERS"));
        permissions.add(new Permission("Get order by id", "/api/v1/orders/{id}", "GET", "ORDERS"));
        permissions.add(new Permission("Get orders with pagination", "/api/v1/orders", "GET", "ORDERS"));
        permissions.add(new Permission("Update order status", "/api/v1/orders/{id}/status", "PUT", "ORDERS"));

        // Delivery management
        permissions.add(new Permission("Assign delivery", "/api/v1/deliveries/assign", "POST", "DELIVERIES"));
        permissions
                .add(new Permission("Update delivery status", "/api/v1/deliveries/{id}/status", "PUT", "DELIVERIES"));
        permissions.add(new Permission("Get delivery by id", "/api/v1/deliveries/{id}", "GET", "DELIVERIES"));
        permissions.add(new Permission("Get deliveries with pagination", "/api/v1/deliveries", "GET", "DELIVERIES"));
        permissions.add(new Permission("Get my deliveries", "/api/v1/deliveries/my", "GET", "DELIVERIES"));

        this.permissionRepository.saveAll(permissions);
    }

    private void initializeRoles() {
        List<Permission> allPermissions = this.permissionRepository.findAll();
        Map<String, List<Permission>> modulePermissions = new HashMap<>();

        // Nhóm các permission theo module
        for (Permission permission : allPermissions) {
            modulePermissions.computeIfAbsent(permission.getModule(), k -> new ArrayList<>()).add(permission);
        }

        // 1. ROLE USER - Chỉ có quyền cơ bản với profile và xem sản phẩm
        Role userRole = new Role();
        userRole.setName("USER");
        userRole.setDescription("Người dùng thông thường với quyền hạn giới hạn");
        userRole.setActive(true);

        List<Permission> userPermissions = new ArrayList<>();
        userPermissions.addAll(modulePermissions.get("PROFILES"));
        userPermissions.add(getPermissionByPath("/api/v1/products/{id}", "GET", allPermissions));
        userPermissions.add(getPermissionByPath("/api/v1/products", "GET", allPermissions));
        userPermissions.add(getPermissionByPath("/api/v1/orders", "POST", allPermissions));
        userPermissions.add(getPermissionByPath("/api/v1/orders/{id}", "GET", allPermissions));
        userPermissions.add(getPermissionByPath("/api/v1/files/{fileName}", "GET", allPermissions));

        userRole.setPermissions(userPermissions);
        this.roleRepository.save(userRole);

        // 2. ROLE DELIVERER - Người giao hàng
        Role delivererRole = new Role();
        delivererRole.setName("DELIVERER");
        delivererRole.setDescription("Người phụ trách giao hàng");
        delivererRole.setActive(true);

        List<Permission> delivererPermissions = new ArrayList<>(userPermissions); // Kế thừa từ USER
        delivererPermissions.addAll(modulePermissions.get("DELIVERIES")); // Thêm quyền quản lý giao hàng

        delivererRole.setPermissions(delivererPermissions);
        this.roleRepository.save(delivererRole);

        // 3. ROLE MANAGER - Quản lý đơn hàng, sản phẩm
        Role managerRole = new Role();
        managerRole.setName("MANAGER");
        managerRole.setDescription("Quản lý hệ thống với quyền quản lý sản phẩm và đơn hàng");
        managerRole.setActive(true);

        List<Permission> managerPermissions = new ArrayList<>(userPermissions); // Kế thừa từ USER
        managerPermissions.addAll(modulePermissions.get("PRODUCTS")); // Thêm quyền quản lý sản phẩm
        managerPermissions.addAll(modulePermissions.get("ORDERS")); // Thêm quyền quản lý đơn hàng
        managerPermissions.addAll(modulePermissions.get("FILES")); // Thêm quyền upload files

        managerRole.setPermissions(managerPermissions);
        this.roleRepository.save(managerRole);

        // 4. ROLE ADMIN - Full quyền
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Quản trị viên hệ thống với toàn quyền");
        adminRole.setActive(true);
        adminRole.setPermissions(allPermissions); // Đầy đủ tất cả quyền

        this.roleRepository.save(adminRole);
    }

    private Permission getPermissionByPath(String path, String method, List<Permission> permissions) {
        return permissions.stream()
                .filter(p -> p.getApiPath().equals(path) && p.getMethod().equals(method))
                .findFirst()
                .orElse(null);
    }

    private void initializeUsers() {
        // Tạo user ADMIN
        User adminUser = createUser(
                "admin@jewelry.com",
                "Admin",
                "123456",
                GenderEnum.MALE,
                30,
                "Quận 1, TP.HCM");
        adminUser.setAvatar("1.png");
        adminUser.setRole(this.roleRepository.findByName("ADMIN"));
        this.userRepository.save(adminUser);

        // Tạo user MANAGER
        User managerUser = createUser(
                "manager@jewelry.com",
                "Store Manager",
                "123456",
                GenderEnum.FEMALE,
                28,
                "Quận 3, TP.HCM");
        managerUser.setAvatar("2.png");
        managerUser.setRole(this.roleRepository.findByName("MANAGER"));
        this.userRepository.save(managerUser);

        // Tạo user DELIVERER
        User delivererUser = createUser(
                "delivery@jewelry.com",
                "Delivery Staff",
                "123456",
                GenderEnum.MALE,
                25,
                "Quận 7, TP.HCM");
        delivererUser.setAvatar("3.png");
        delivererUser.setRole(this.roleRepository.findByName("DELIVERER"));
        this.userRepository.save(delivererUser);

        // Tạo user thường
        User normalUser = createUser(
                "user@jewelry.com",
                "Normal User",
                "123456",
                GenderEnum.FEMALE,
                22,
                "Quận 2, TP.HCM");
        normalUser.setAvatar("4.png");
        normalUser.setRole(this.roleRepository.findByName("USER"));
        this.userRepository.save(normalUser);
    }

    private User createUser(String email, String name, String password, GenderEnum gender, int age, String address) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(this.passwordEncoder.encode(password));
        user.setGender(gender);
        user.setAge(age);
        user.setAddress(address);
        return user;
    }
}