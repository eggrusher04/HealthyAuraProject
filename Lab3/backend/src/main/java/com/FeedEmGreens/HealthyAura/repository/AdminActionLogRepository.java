package com.FeedEmGreens.HealthyAura.repository;

import com.FeedEmGreens.HealthyAura.entity.AdminActionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for performing CRUD and custom queries on
 * {@link com.FeedEmGreens.HealthyAura.entity.AdminActionLog} entities.
 *
 * <p>This repository handles database interactions related to administrative
 * actions, including logging tag edits, reward updates, and moderation events.
 * It is managed automatically by Spring Data JPA, providing default methods such
 * as {@code save()}, {@code findById()}, and {@code findAll()}.</p>
 *
 * <p>Custom query methods allow retrieving logs by admin username,
 * ordered chronologically.</p>
 *
 * @see com.FeedEmGreens.HealthyAura.entity.AdminActionLog
 * @see org.springframework.data.jpa.repository.JpaRepository
 *
 * @version 1.0
 * @since 2025-11-07
 */
@Repository
public interface AdminActionLogRepository extends JpaRepository<AdminActionLog, Long> {

    /**
     * Retrieves all admin action logs performed by a specific administrator,
     * sorted by timestamp in descending order (newest first).
     *
     * @param adminUsername the username of the administrator
     * @return a list of {@link AdminActionLog} entries belonging to the admin
     */
    List<AdminActionLog> findByAdminUsernameOrderByTimestampDesc(String adminUsername);

    /**
     * Retrieves the 10 most recent admin action logs for a given administrator.
     * <p>This is useful for dashboard summaries or recent activity panels.</p>
     *
     * @param adminUsername the username of the administrator
     * @return the 10 most recent {@link AdminActionLog} entries by that admin
     */
    List<AdminActionLog> findTop10ByAdminUsernameOrderByTimestampDesc(String adminUsername);
}
