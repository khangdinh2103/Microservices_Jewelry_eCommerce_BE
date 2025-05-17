package com.iuh.edu.fit.BEJewelry.Architecture.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.request.ReqChangePasswordDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.request.ReqOccasionReminderDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResCreateUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResUpdateUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResultPaginationDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.scheduler.OccasionReminderScheduler;
import com.iuh.edu.fit.BEJewelry.Architecture.service.FileStorageService;
import com.iuh.edu.fit.BEJewelry.Architecture.service.OccasionReminderService;
import com.iuh.edu.fit.BEJewelry.Architecture.service.UserService;
import com.iuh.edu.fit.BEJewelry.Architecture.util.SecurityUtil;
import com.iuh.edu.fit.BEJewelry.Architecture.util.annotation.ApiMessage;
import com.iuh.edu.fit.BEJewelry.Architecture.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final OccasionReminderService occasionReminderService;
    private final ApplicationContext applicationContext;

    public UserController(
            UserService userService, 
            PasswordEncoder passwordEncoder,
            FileStorageService fileStorageService,
            OccasionReminderService occasionReminderService,
            ApplicationContext applicationContext) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
        this.occasionReminderService = occasionReminderService;
        this.applicationContext = applicationContext;
    }

    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }

    @GetMapping("/users")
    @ApiMessage("Fetches all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spec, Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
    }

    @PostMapping("/users")
    @ApiMessage("Create a new user")
    public ResponseEntity<ResCreateUserDTO> createNewUser(@Valid @RequestBody User postManUser)
            throws IdInvalidException {
        boolean isEmailExist = this.userService.isEmailExist(postManUser.getEmail());
        if (isEmailExist) {
            throw new IdInvalidException("Email " + postManUser.getEmail() + " đã tồn tại. Vui lòng chọn email khác!");
        }

        String hashPassword = this.passwordEncoder.encode(postManUser.getPassword());
        postManUser.setPassword(hashPassword);
        User newUser = this.userService.handleCreateUser(postManUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(this.userService.convertToResCreateUserDTO(newUser));
    }

    @PutMapping("/users")
    @ApiMessage("Update a user")
    public ResponseEntity<ResUpdateUserDTO> updateUser(@RequestBody User user) throws IdInvalidException {
        User updateUser = this.userService.handleUpdateUser(user);
        if (updateUser == null) {
            throw new IdInvalidException("User với id = " + user.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(this.userService.convertToResUpdateUserDTO(updateUser));
    }

    @DeleteMapping("/users/{id}")
    @ApiMessage("Delete a user")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long id) throws IdInvalidException {
        User currentUser = this.userService.fetchUserById(id);
        if (currentUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }
        this.userService.handleDeleteUser(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/profile")
    public ResponseEntity<ResUserDTO> getUserProfile() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        User user = userService.handleGetUserByUserName(email);
        return ResponseEntity.ok(userService.convertToResUserDTO(user));
    }

    @PutMapping("/profile")
    public ResponseEntity<ResUpdateUserDTO> updateUserProfile(@RequestBody User user) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        User existingUser = userService.handleGetUserByUserName(email);
        user.setId(existingUser.getId());
        User updatedUser = userService.handleUpdateUser(user);
        return ResponseEntity.ok(userService.convertToResUpdateUserDTO(updatedUser));
    }

    @PutMapping("/profile/change-password")
    public ResponseEntity<Void> changeUserPassword(@Valid @RequestBody ReqChangePasswordDTO reqChangePasswordDTO) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        User user = userService.handleGetUserByUserName(email);

        if (!passwordEncoder.matches(reqChangePasswordDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không chính xác");
        }

        user.setPassword(passwordEncoder.encode(reqChangePasswordDTO.getNewPassword()));
        userService.handleUpdateUser(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/profile/avatar")
    public ResponseEntity<Map<String, String>> updateAvatar(@RequestParam("file") MultipartFile file) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        User user = userService.handleGetUserByUserName(email);

        String fileName = fileStorageService.storeFile(file);
        user.setAvatar(fileName);
        userService.handleUpdateUser(user);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/v1/files/")
                .path(fileName)
                .toUriString();

        Map<String, String> response = new HashMap<>();
        response.put("avatar", fileName);
        response.put("avatarUrl", fileDownloadUri);

        return ResponseEntity.ok(response);
    }
    
    // Special Occasion Reminders endpoints
    
    @GetMapping("/profile/occasions")
    @ApiMessage("Get all occasion reminders for current user")
    public ResponseEntity<?> getUserOccasionReminders(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return ResponseEntity.ok(occasionReminderService.getUserOccasionReminders(email, pageable));
    }
    
    @GetMapping("/profile/occasions/upcoming")
    @ApiMessage("Get upcoming occasion reminders for current user")
    public ResponseEntity<?> getUpcomingOccasions() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return ResponseEntity.ok(occasionReminderService.getUpcomingOccasions(email));
    }
    
    @PostMapping("/profile/occasions")
    @ApiMessage("Create a new occasion reminder")
    public ResponseEntity<?> createOccasionReminder(@Valid @RequestBody ReqOccasionReminderDTO request) {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(occasionReminderService.createOccasionReminder(email, request));
    }
    
    @PutMapping("/profile/occasions/{id}")
    @ApiMessage("Update an occasion reminder")
    public ResponseEntity<?> updateOccasionReminder(
            @PathVariable("id") Long id, 
            @Valid @RequestBody ReqOccasionReminderDTO request) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        return ResponseEntity.ok(occasionReminderService.updateOccasionReminder(email, id, request));
    }
    
    @DeleteMapping("/profile/occasions/{id}")
    @ApiMessage("Delete an occasion reminder")
    public ResponseEntity<?> deleteOccasionReminder(@PathVariable("id") Long id) throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        occasionReminderService.deleteOccasionReminder(email, id);
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/profile/occasions/test-reminder")
    @ApiMessage("Test occasion reminder email")
    public ResponseEntity<?> testOccasionReminder() {
        String email = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));
        
        try {
            // Get the scheduler bean and run it manually
            OccasionReminderScheduler scheduler = applicationContext.getBean(OccasionReminderScheduler.class);
            scheduler.sendOccasionReminders();
            return ResponseEntity.ok("Đã kích hoạt kiểm tra nhắc nhở dịp đặc biệt");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi gửi nhắc nhở: " + e.getMessage());
        }
    }
}