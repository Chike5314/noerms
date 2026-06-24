package com.noerms.modules.auth.dto;
import lombok.*;
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class UserDto {
    private Long id;
    private String email;
    private String username;
    private String fullName;
    private String role;
    private Boolean active;
}
