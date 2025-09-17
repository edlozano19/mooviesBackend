package com.moovies.mooviesBackend.user.entity;

import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor 
@AllArgsConstructor
public class UserEntityExample {
    
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    @JsonIgnore // IMPORTANT: Never expose password in JSON responses
    private String passwordHash;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name") 
    private String lastName;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false) 
    private LocalDateTime updatedAt;

    // Helper method to set password (will be hashed in service layer)
    public void setPassword(String plainPassword) {
        // This will be handled in the service layer with PasswordEncoder
        // Don't hash here - keep entity clean
        this.passwordHash = plainPassword; // Temporary - will be hashed in service
    }
}
