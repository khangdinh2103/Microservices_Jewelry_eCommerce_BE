package com.iuh.edu.fit.BEJewelry.Architecture.domain.response;

import java.time.Instant;

import com.iuh.edu.fit.BEJewelry.Architecture.util.constant.GenderEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private int age;
    private String address;
    private Instant updatedAt;
}