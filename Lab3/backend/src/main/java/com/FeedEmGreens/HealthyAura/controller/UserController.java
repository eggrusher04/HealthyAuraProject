package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile/me")
    public ResponseEntity<?> getUserProfile() {
        // Retrieve the authenticated user's name (username) from the Security Context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        Optional<Users> userOpt = userService.findByUsername(username);

        if (userOpt.isPresent()) {
            Users user = userOpt.get();
            // IMPORTANT: Never return the password hash.
            // Assuming your User entity has fields like username, email, preferences, and excludes the password from its DTO or JSON serialization.
            return ResponseEntity.ok(user);
        }
        // Should theoretically not happen if the token is valid
        return ResponseEntity.notFound().build();
    }
}