package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.AdminActivityLogDTO;
import com.FeedEmGreens.HealthyAura.entity.AdminActivityLog;
import com.FeedEmGreens.HealthyAura.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/activity-logs")
    public ResponseEntity<?> getActivityLogs(HttpServletRequest request) {
        try {
            List<AdminActivityLog> logs = adminService.getAllActivityLogs();
            
            List<AdminActivityLogDTO> logDTOs = logs.stream().map(log -> {
                AdminActivityLogDTO dto = new AdminActivityLogDTO();
                dto.setId(log.getId());
                dto.setAdminUsername(log.getAdmin().getUsername());
                dto.setAction(log.getAction());
                dto.setDescription(log.getDescription());
                dto.setTimestamp(log.getTimestamp());
                dto.setIpAddress(log.getIpAddress());
                return dto;
            }).collect(Collectors.toList());

            // Log the activity
            // Note: You'll need to get the admin from JWT token in a real implementation
            // adminService.logAdminActivity(admin, "VIEW_LOGS", "Viewed all activity logs", request);

            return ResponseEntity.ok(logDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to fetch activity logs: " + e.getMessage());
        }
    }
}