package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.dto.CrowdDisplayDTO;
import org.springframework.stereotype.Service;
import java.time.*;

/**
 * Service class responsible for estimating and displaying real-time crowd levels
 * for eateries within the HealthyAura application.
 *
 * <p>This class provides a simulated, time-based estimation of crowd density and
 * expected waiting times for eateries, differentiated by weekday and weekend patterns.</p>
 *
 * <p>Each estimation includes:
 * <ul>
 *   <li>A qualitative crowd level (e.g., “Low”, “Moderate”, “Busy”, “Very Busy”)</li>
 *   <li>An approximate queue duration (in minutes)</li>
 *   <li>A color code for easy UI visualization</li>
 * </ul>
 * </p>
 *
 * <p>This service can be extended in the future to integrate with:
 * <ul>
 *   <li>IoT-based people counters</li>
 *   <li>Mobile location or occupancy APIs</li>
 *   <li>Machine learning–based real-time demand prediction</li>
 * </ul>
 * </p>
 *
 * @see com.FeedEmGreens.HealthyAura.dto.CrowdDisplayDTO
 * @version 1.0
 * @since 2025-11-07
 */
@Service
public class CrowdService {

    /**
     * Determines the estimated crowd level and queue time for a given eatery.
     *
     * <p>This implementation uses a rule-based approach depending on:
     * <ul>
     *   <li>Current day of the week (weekend vs weekday)</li>
     *   <li>Current time (lunch and dinner peaks)</li>
     * </ul>
     * </p>
     *
     * <p>Color codes follow the general UI convention:
     * <ul>
     *   <li><b>Green</b> – Low crowd</li>
     *   <li><b>Yellow</b> – Moderate crowd</li>
     *   <li><b>Orange</b> – Busy</li>
     *   <li><b>Red</b> – Very busy</li>
     * </ul>
     * </p>
     *
     * @param eateryId the ID of the eatery (currently unused, reserved for future data integration)
     * @return a {@link CrowdDisplayDTO} object containing crowd level, estimated wait time, and color code
     */
    public CrowdDisplayDTO getCrowdStatus(Long eateryId) {
        LocalTime now = LocalTime.now();
        DayOfWeek day = LocalDate.now().getDayOfWeek();

        String crowdLevel;
        int queueMinutes;
        String colorCode;

        // Weekend crowd pattern
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            if (isBetween(now, 12, 0, 14, 0)) {
                crowdLevel = "Very Busy";
                queueMinutes = 25;
                colorCode = "#E74C3C"; // Red
            } else if (isBetween(now, 18, 0, 20, 0)) {
                crowdLevel = "Busy";
                queueMinutes = 20;
                colorCode = "#F39C12"; // Orange
            } else {
                crowdLevel = "Moderate";
                queueMinutes = 10;
                colorCode = "#F1C40F"; // Yellow
            }
        }

        // Weekday crowd pattern
        else {
            if (isBetween(now, 12, 0, 14, 0)) {
                crowdLevel = "Busy";
                queueMinutes = 15;
                colorCode = "#F39C12"; // Orange
            } else if (isBetween(now, 18, 0, 20, 0)) {
                crowdLevel = "Moderate";
                queueMinutes = 10;
                colorCode = "#F1C40F"; // Yellow
            } else {
                crowdLevel = "Low";
                queueMinutes = 5;
                colorCode = "#27AE60"; // Green
            }
        }

        return new CrowdDisplayDTO(crowdLevel, queueMinutes, colorCode);
    }

    /**
     * Helper method to check if the current time falls between two specified times.
     *
     * <p>This function is used to determine whether the current time
     * is within typical lunch or dinner peak periods.</p>
     *
     * @param now the current system time
     * @param startHour the start hour of the period
     * @param startMin the start minute of the period
     * @param endHour the end hour of the period
     * @param endMin the end minute of the period
     * @return {@code true} if the current time is between the start and end time, otherwise {@code false}
     */
    private boolean isBetween(LocalTime now, int startHour, int startMin, int endHour, int endMin) {
        return now.isAfter(LocalTime.of(startHour, startMin))
                && now.isBefore(LocalTime.of(endHour, endMin));
    }
}
