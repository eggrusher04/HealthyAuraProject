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

/**
 * Controller that manages access to administrative action logs.
 *
 * <p>This controller provides endpoints for:
 * <ul>
 *     <li>Viewing all administrative action logs (for system-wide auditing)</li>
 *     <li>Viewing personal logs for the currently authenticated admin</li>
 * </ul>
 *
 * <p>Access to these endpoints is restricted to users with the <strong>ADMIN</strong> role.</p>
 *
 * @version 1.0
 * @since 2025-11-07
 */

@RestController
@RequestMapping("/admin/logs")
public class AdminLogController {

    /**
     * Repository for retrieving and managing {@link AdminActionLog} records.
     */
    @Autowired
    private AdminActionLogRepository adminActionLogRepository;

    /**
     * Retrieves all administrative action logs in the system.
     *
     * <p>This endpoint is accessible only by administrators and provides
     * a complete list of all actions recorded in the {@link AdminActionLogRepository}.
     * It is typically used for system-level monitoring or audit reviews.</p>
     *
     * @return a {@link ResponseEntity} containing a list of all {@link AdminActionLog} entries
     */

    // View all logs(Admins only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminActionLog>> getAllLogs() {
        List<AdminActionLog> logs = adminActionLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }

    /**
     * Retrieves all administrative action logs associated with the currently authenticated admin.
     *
     * <p>This allows an admin user to view their own recent activities
     * (e.g., moderation decisions, flag reviews, reward management actions).
     * Results are sorted in descending order of timestamp.</p>
     *
     * @return a {@link ResponseEntity} containing the list of {@link AdminActionLog}
     *         entries created by the current administrator
     */

    // View own logs(Admins only)
    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AdminActionLog>> getMyLogs() {
        String admin = SecurityContextHolder.getContext().getAuthentication().getName();
        List<AdminActionLog> logs = adminActionLogRepository.findByAdminUsernameOrderByTimestampDesc(admin);
        return ResponseEntity.ok(logs);
    }
}


