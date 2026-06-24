package com.noerms.modules.auth.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class LoginRequest {
    @Email(message="Valid email required") @NotBlank
    private String email;
    @NotBlank(message="Password required")
    private String password;
}
