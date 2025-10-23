package com.moovies.mooviesBackend.auth.dto;

import com.moovies.mooviesBackend.user.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Long userId;
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
}
