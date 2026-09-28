package com.tourism.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 60, message = "Username must be between 3 and 60 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    private String phone;

    /**
     * Optional. Only ADMIN accounts may create TOUR_OPERATOR or ADMIN users via the
     * admin user-management endpoint; public self-registration always forces CUSTOMER,
     * regardless of what is sent here (never trust frontend roles).
     */
    private String role;
}
