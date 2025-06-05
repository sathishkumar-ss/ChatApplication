package com.example.chat.controller;

import com.example.chat.dto.UserRegistrationDto;
import com.example.chat.dto.UserLoginDto;
import com.example.chat.model.User;
import com.example.chat.service.UserService;
import com.example.chat.validation.ValidationGroups;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login(Model model) {
        if (!model.containsAttribute("userLoginDto")) {
            model.addAttribute("userLoginDto", new UserLoginDto());
        }
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        if (!model.containsAttribute("userRegistrationDto")) {
            model.addAttribute("userRegistrationDto", new UserRegistrationDto());
        }
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Validated(ValidationGroups.RegularRegistration.class) @ModelAttribute("userRegistrationDto") UserRegistrationDto registrationDto,
                              BindingResult result,
                              RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userRegistrationDto", result);
            redirectAttributes.addFlashAttribute("userRegistrationDto", registrationDto);
            return "redirect:/register";
        }

        try {
            userService.registerUser(registrationDto);
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login with your credentials.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("userRegistrationDto", registrationDto);
            return "redirect:/register";
        }
    }

    @GetMapping("/chat")
    public String chat(Authentication authentication, Model model) {
        if (authentication != null) {
            if (authentication.getPrincipal() instanceof OAuth2User) {
                // OAuth2 user
                OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                String email = oauth2User.getAttribute("email");
                String name = oauth2User.getAttribute("name");
                String picture = oauth2User.getAttribute("picture");
                
                // Save OAuth user to database
                User user = userService.findOrCreateOAuthUser(email, name, picture, "google", oauth2User.getAttribute("sub"));
                
                model.addAttribute("username", user.getUsername());
                model.addAttribute("userEmail", user.getEmail());
                model.addAttribute("userPicture", user.getPictureUrl());
                model.addAttribute("fullName", user.getFullName());
            } else if (authentication.getPrincipal() instanceof UserDetails) {
                // Regular user
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                User user = userService.findByUsername(userDetails.getUsername()).orElse(null);
                
                if (user != null) {
                    model.addAttribute("username", user.getUsername());
                    model.addAttribute("userEmail", user.getEmail());
                    model.addAttribute("userPicture", user.getPictureUrl());
                    model.addAttribute("fullName", user.getFullName());
                }
            }
        }
        return "chat";
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/chat";
        }
        return "redirect:/login";
    }
} 