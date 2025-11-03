package com.FeedEmGreens.HealthyAura.service;

import com.FeedEmGreens.HealthyAura.dto.CrowdDisplayDTO;
import org.springframework.stereotype.Service;
import java.time.*;

@Service
public class CrowdService {

    public CrowdDisplayDTO getCrowdStatus(Long eateryId) {
        LocalTime now = LocalTime.now();
        DayOfWeek day = LocalDate.now().getDayOfWeek();

        String crowdLevel;
        int queueMinutes;
        String colorCode;

        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            if (isBetween(now, 12, 0, 14, 0)) {
                crowdLevel = "Very Busy";
                queueMinutes = 25;
                colorCode = "#E74C3C"; //  Red
            } else if (isBetween(now, 18, 0, 20, 0)) {
                crowdLevel = "Busy";
                queueMinutes = 20;
                colorCode = "#F39C12"; // Orange
            } else {
                crowdLevel = "Moderate";
                queueMinutes = 10;
                colorCode = "#F1C40F"; //  Yellow
            }
        } else {
            if (isBetween(now, 12, 0, 14, 0)) {
                crowdLevel = "Busy";
                queueMinutes = 15;
                colorCode = "#F39C12"; //  Orange
            } else if (isBetween(now, 18, 0, 20, 0)) {
                crowdLevel = "Moderate";
                queueMinutes = 10;
                colorCode = "#F1C40F"; //  Yellow
            } else {
                crowdLevel = "Low";
                queueMinutes = 5;
                colorCode = "#27AE60"; //  Green
            }
        }

        return new CrowdDisplayDTO(crowdLevel, queueMinutes, colorCode);
    }

    private boolean isBetween(LocalTime now, int startHour, int startMin, int endHour, int endMin) {
        return now.isAfter(LocalTime.of(startHour, startMin))
                && now.isBefore(LocalTime.of(endHour, endMin));
    }
}
