package com.iuh.edu.fit.BEJewelry.Architecture.domain.response;

import java.time.Instant;

import com.iuh.edu.fit.BEJewelry.Architecture.util.constant.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResUserDTO {
    private long id;
    private String email;
    private String name;
    private GenderEnum gender;
    private int age;
    private String address;
    private String avatar;
    private String avatarUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private RoleUser role;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RoleUser {
        private long id;
        private String name;
    }
}