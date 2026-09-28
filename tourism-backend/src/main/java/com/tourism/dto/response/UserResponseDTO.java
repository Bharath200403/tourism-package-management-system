package com.tourism.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String role;
    private String status;
    private String createdAt;
}
