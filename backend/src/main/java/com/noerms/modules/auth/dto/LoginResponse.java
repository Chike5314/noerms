package com.noerms.modules.auth.dto;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LoginResponse {
    private Boolean success;
    private String token;
    private Long userId;
    private String email;
    private String fullName;
    private String role;
    private String message;
}
