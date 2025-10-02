package com.moovies.mooviesBackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    @NotBlank
    @Size(min = 3, max = 100, message = "Username or email must be between 3 and 100 characters")
    private String usernameOrEmail;

    @NotBlank
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long")
    private String password;
    
}
