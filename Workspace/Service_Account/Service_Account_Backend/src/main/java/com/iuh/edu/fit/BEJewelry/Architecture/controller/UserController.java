package com.iuh.edu.fit.BEJewelry.Architecture.controller;

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
import org.springframework.web.bind.annotation.RestController;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.User;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.request.ReqChangePasswordDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResCreateUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResUpdateUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResUserDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResultPaginationDTO;
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

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
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

    // fetch user by id
    @GetMapping("/users/{id}")
    @ApiMessage("fetch user by id")
    public ResponseEntity<ResUserDTO> getUserById(@PathVariable("id") long id) throws IdInvalidException {
        User fetchUser = this.userService.fetchUserById(id);
        if (fetchUser == null) {
            throw new IdInvalidException("User với id = " + id + " không tồn tại");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.userService.convertToResUserDTO(fetchUser));
    }

    // fetch all users
    @GetMapping("/users")
    @ApiMessage("Fetches all users")
    public ResponseEntity<ResultPaginationDTO> getAllUser(@Filter Specification<User> spec,
            Pageable pageable) {
        return ResponseEntity.status(HttpStatus.OK).body(this.userService.fetchAllUser(spec, pageable));
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
}
