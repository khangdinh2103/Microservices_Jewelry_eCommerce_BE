package com.iuh.edu.fit.BEJewelry.Architecture.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqResetPasswordDTO {
    @NotBlank(message = "Token không được để trống")
    private String token;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    private String newPassword;
}
