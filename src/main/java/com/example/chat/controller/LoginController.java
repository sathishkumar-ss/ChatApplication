package com.example.chat.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/chat")
    public String chat(Authentication authentication, Model model) {
        if (authentication != null && authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String username = oauth2User.getAttribute("name");
            if (username == null || username.isEmpty()) {
                username = oauth2User.getAttribute("email");
            }
            model.addAttribute("username", username);
            model.addAttribute("userEmail", oauth2User.getAttribute("email"));
            model.addAttribute("userPicture", oauth2User.getAttribute("picture"));
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