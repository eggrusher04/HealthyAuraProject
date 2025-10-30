package com.FeedEmGreens.HealthyAura.controller;

import com.FeedEmGreens.HealthyAura.dto.AdminActivityLogDTO;
import com.FeedEmGreens.HealthyAura.entity.AdminActivityLog;
import com.FeedEmGreens.HealthyAura.repository.AdminActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/logs")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminLogsController {

    @Autowired
    private AdminActivityLogRepository adminActivityLogRepository;

    @GetMapping
    public ResponseEntity<List<AdminActivityLogDTO>> getAllLogs() {
        try {
            List<AdminActivityLog> logs = adminActivityLogRepository.findAllByOrderByTimestampDesc();
            
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
            
            return ResponseEntity.ok(logDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}