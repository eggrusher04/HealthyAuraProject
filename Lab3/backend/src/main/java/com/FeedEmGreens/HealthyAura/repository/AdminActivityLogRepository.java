package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.AdminActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdminActivityLogRepository extends JpaRepository<AdminActivityLog, Long> {
    List<AdminActivityLog> findAllByOrderByTimestampDesc();
    List<AdminActivityLog> findByAdminIdOrderByTimestampDesc(Long adminId);
}