// backend/src/main/java/com/FeedEmGreens/HealthyAura/service/AdminActivityLogService.java

package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.entity.Admin;
import com.FeedEmGreens.HealthyAura.entity.AdminActivityLog;
import com.FeedEmGreens.HealthyAura.repository.AdminActivityLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

@Service
public class AdminActivityLogService {

    @Autowired
    private AdminActivityLogRepository logRepository;

    public void logAdminAction(Admin admin, String action, String description, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        AdminActivityLog log = new AdminActivityLog(admin, action, description, ipAddress);
        log.setTimestamp(LocalDateTime.now());
        logRepository.save(log);
    }
}