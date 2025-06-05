package com.example.chat.service;

import com.example.chat.dto.UserRegistrationDto;
import com.example.chat.model.User;
import com.example.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public User registerUser(UserRegistrationDto registrationDto) throws RuntimeException {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Check if passwords match
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());
        
        return userRepository.save(user);
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        Optional<User> user = userRepository.findByUsername(usernameOrEmail);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(usernameOrEmail);
        }
        return user;
    }
    
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public User findOrCreateOAuthUser(String email, String name, String picture, String provider, String oauthId) {
        Optional<User> existingUser = userRepository.findByOauthProviderAndOauthId(provider, oauthId);
        
        if (existingUser.isPresent()) {
            return existingUser.get();
        }
        
        // Check if user exists with same email but different provider
        Optional<User> userByEmail = userRepository.findByEmail(email);
        if (userByEmail.isPresent()) {
            User user = userByEmail.get();
            user.setOauthProvider(provider);
            user.setOauthId(oauthId);
            user.setPictureUrl(picture);
            return userRepository.save(user);
        }
        
        // Create new OAuth user
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setFullName(name);
        newUser.setUsername(generateUsernameFromEmail(email));
        newUser.setOauthProvider(provider);
        newUser.setOauthId(oauthId);
        newUser.setPictureUrl(picture);
        newUser.setPassword(null); // OAuth users don't have passwords
        
        return userRepository.save(newUser);
    }
    
    private String generateUsernameFromEmail(String email) {
        String baseUsername = email.substring(0, email.indexOf('@')).replaceAll("[^a-zA-Z0-9_]", "");
        String username = baseUsername;
        int counter = 1;
        
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }
} 