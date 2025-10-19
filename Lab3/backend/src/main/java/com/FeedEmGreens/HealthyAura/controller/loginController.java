package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.LoginRequest;
import com.FeedEmGreens.HealthyAura.dto.SignupRequest;
import com.FeedEmGreens.HealthyAura.dto.AuthResponse;
import com.FeedEmGreens.HealthyAura.entity.Users;
import com.FeedEmGreens.HealthyAura.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* Due to the springboot framework, controllers are kept thin to handle HTTP requests */

@RestController
@RequestMapping("/auth")
public class loginController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<Users> signup(@RequestBody SignupRequest req){
        return ResponseEntity.ok(authService.signup(req.getEmail(), req.getUsername(), req.getPassword(), "USER"));
    }

    //Ensure ONLY admin can create an account(admin has to log in first)
    @PostMapping("/admin/signup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Users> createAdmin(@RequestBody SignupRequest req){
        return ResponseEntity.ok(authService.createAdmin(req.getEmail(), req.getUsername(), req.getPassword(), "ADMIN"));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest req){
        return ResponseEntity.ok(authService.login(req.getUsername(), req.getPassword()));
    }
}
