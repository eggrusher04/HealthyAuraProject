package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.entity.AdminActionLog;
import com.FeedEmGreens.HealthyAura.repository.AdminActionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/logs")
public class AdminLogController {

    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    // View all logs(Admins only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminActionLog>> getAllLogs() {
        List<AdminActionLog> logs = adminActionLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }

    // View own logs(Admins only)
    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminActionLog>> getMyLogs() {
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();
        List<AdminActionLog> logs = adminActionLogRepository.findByAdminUsernameOrderByTimestampDesc(admin);
        return ResponseEntity.ok(logs);
    }
}


