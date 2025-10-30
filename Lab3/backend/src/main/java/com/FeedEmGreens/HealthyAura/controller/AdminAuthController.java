package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.AdminLoginRequest;
import com.FeedEmGreens.HealthyAura.dto.AuthResponse;
import com.FeedEmGreens.HealthyAura.entity.Admin;
import com.FeedEmGreens.HealthyAura.service.AdminService;
import com.FeedEmGreens.HealthyAura.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

@RestController
@RequestMapping("/api/admin/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminAuthController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AdminLoginRequest loginRequest, HttpServletRequest request) {
        try {
            Optional<Admin> adminOpt = adminService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
            
            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                String token = jwtUtil.generateToken(admin.getUsername(), "ADMIN");
                
                // Log the login activity
                adminService.logAdminActivity(admin, "LOGIN", "Admin logged in successfully", request);
                
                AuthResponse response = new AuthResponse(token, "Admin login successful", admin.getUsername());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body("Invalid admin credentials");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutAdmin(HttpServletRequest request) {
        String token = jwtUtil.resolveToken(request);
        if (token != null && jwtUtil.validateToken(token)) {
            String username = jwtUtil.getUsernameFromToken(token);
            Optional<Admin> adminOpt = adminService.findByUsername(username); // You need a findByUsername in AdminService

            if (adminOpt.isPresent()) {
                Admin admin = adminOpt.get();
                adminService.logAdminActivity(admin, "LOGOUT", "Admin logged out successfully", request);
                return ResponseEntity.ok("Logged out successfully");
            }
        }
        // If no valid token or admin not found (e.g., token expired or tampered), still return OK for a logout request
        return ResponseEntity.ok("Logout request processed");
    }
}