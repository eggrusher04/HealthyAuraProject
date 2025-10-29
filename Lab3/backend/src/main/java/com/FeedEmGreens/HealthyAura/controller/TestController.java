package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.entity.Admin;
import com.FeedEmGreens.HealthyAura.repository.AdminRepository;
import com.FeedEmGreens.HealthyAura.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/auth-status")
    public Map<String, Object> getAuthStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // Check admins
        long adminCount = adminRepository.count();
        status.put("adminsCount", adminCount);
        status.put("admins", adminRepository.findAll());
        
        // Check users
        long userCount = userRepository.count();
        status.put("usersCount", userCount);
        status.put("users", userRepository.findAll());
        
        status.put("message", "Authentication system status");
        
        return status;
    }
}