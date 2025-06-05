package com.example.chat.dto;

import com.example.chat.validation.ValidationGroups;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers, and underscores")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required", groups = ValidationGroups.RegularRegistration.class)
    @Size(min = 8, max = 128, message = "Password must be between 8 and 128 characters", groups = ValidationGroups.RegularRegistration.class)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]+$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character",
             groups = ValidationGroups.RegularRegistration.class)
    private String password;
    
    @NotBlank(message = "Please confirm your password", groups = ValidationGroups.RegularRegistration.class)
    private String confirmPassword;
    
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]*$", message = "Full name can only contain letters and spaces")
    private String fullName;
} 