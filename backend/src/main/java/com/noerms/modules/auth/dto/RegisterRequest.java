package com.noerms.modules.auth.dto;
import jakarta.validation.constraints.*;
import lombok.Data;
@Data
public class RegisterRequest {
    @Email @NotBlank private String email;
    @NotBlank @Size(min=3,max=50) private String username;
    @NotBlank @Size(min=8) private String password;
    @NotBlank private String fullName;
    private String phoneNumber;
}
