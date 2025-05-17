package com.iuh.edu.fit.BEJewelry.Architecture.domain.response;

import com.iuh.edu.fit.BEJewelry.Architecture.util.constant.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

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