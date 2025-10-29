package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.entity.Admin;
import com.FeedEmGreens.HealthyAura.entity.AdminActivityLog;
import com.FeedEmGreens.HealthyAura.repository.AdminRepository;
import com.FeedEmGreens.HealthyAura.repository.AdminActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AdminActivityLogRepository adminActivityLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<Admin> findByUsername(String username) {
        return adminRepository.findByUsername(username);
    }

    public Optional<Admin> authenticate(String username, String password) {
        System.out.println("=== ADMIN AUTHENTICATION DEBUG ===");
        System.out.println("Username: " + username);
        System.out.println("Password provided: '" + password + "'");
        System.out.println("Password length: " + password.length());
        System.out.println("Password bytes: " + java.util.Arrays.toString(password.getBytes()));
        
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        
        if (adminOpt.isPresent()) {
            Admin admin = adminOpt.get();
            System.out.println("Admin found: " + admin.getUsername());
            System.out.println("Stored hash: " + admin.getPassword());
            System.out.println("Stored hash length: " + admin.getPassword().length());
            
            // Test what the password encoder generates for "admin123"
            String testHash = passwordEncoder.encode("admin123");
            System.out.println("Test hash for 'admin123': " + testHash);
            
            // Test direct comparison
            boolean directMatch = password.equals("admin123");
            System.out.println("Direct password match: " + directMatch);
            
            // Test if the stored hash matches "admin123"
            boolean storedHashMatchesAdmin123 = passwordEncoder.matches("admin123", admin.getPassword());
            System.out.println("Stored hash matches 'admin123': " + storedHashMatchesAdmin123);
            
            // Test if provided password matches stored hash
            boolean passwordMatches = passwordEncoder.matches(password, admin.getPassword());
            System.out.println("Provided password matches stored hash: " + passwordMatches);
            
            if (passwordMatches) {
                System.out.println("ADMIN AUTHENTICATION SUCCESSFUL");
                return adminOpt;
            } else {
                System.out.println("ADMIN AUTHENTICATION FAILED");
                System.out.println("Possible issues:");
                System.out.println("- Password encoding mismatch");
                System.out.println("- Database hash corrupted");
                System.out.println("- Password contains hidden characters");
            }
        } else {
            System.out.println("ADMIN NOT FOUND: " + username);
        }
        
        return Optional.empty();
    }

    public void logAdminActivity(Admin admin, String action, String description, HttpServletRequest request) {
        String ipAddress = getClientIpAddress(request);
        AdminActivityLog log = new AdminActivityLog(admin, action, description, ipAddress);
        log.setTimestamp(LocalDateTime.now());
        adminActivityLogRepository.save(log);
        System.out.println("Admin activity logged: " + action + " by " + admin.getUsername());
    }

    public List<AdminActivityLog> getAllActivityLogs() {
        return adminActivityLogRepository.findAllByOrderByTimestampDesc();
    }

    public List<AdminActivityLog> getActivityLogsByAdmin(Long adminId) {
        return adminActivityLogRepository.findByAdminIdOrderByTimestampDesc(adminId);
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}