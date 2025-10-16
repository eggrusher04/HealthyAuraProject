package com.FeedEmGreens.HealthyAura.mapper;

import com.FeedEmGreens.HealthyAura.dto.EateryDTO;
import com.FeedEmGreens.HealthyAura.model.GovLicensedEatingResponse;

public class GovLicensedEatingMapper {

    public static EateryDTO toDTO(GovLicensedEatingResponse.Record record) {
        if (record == null) {
            return null;
        }

        EateryDTO dto = new EateryDTO();
        dto.setLicenceNumber(safeValue(record.getLicenceNumber()));
        dto.setLicenseeName(safeValue(record.getLicenseeName()));
        dto.setPremisesAddress(safeValue(record.getPremisesAddress()));
        dto.setGrade(safeValue(record.getGrade()));
        dto.setDemeritPoints(safeValue(record.getDemeritPoints()));
        dto.setLicenceStatus(deriveStatus(record));
        return dto;
    }

    // Handle null or "na" gracefully
    private static String safeValue(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("na")) {
            return "N/A";
        }
        return value;
    }

    // Optionally derive a "status" if your DTO needs it
    private static String deriveStatus(GovLicensedEatingResponse.Record record) {
        // you can modify this if API later provides a field for status
        return "Active";
    }
}
