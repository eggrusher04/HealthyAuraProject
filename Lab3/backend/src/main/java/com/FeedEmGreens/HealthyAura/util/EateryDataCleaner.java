package com.FeedEmGreens.HealthyAura.util;

import com.FeedEmGreens.HealthyAura.model.GovLicensedEatingResponse;
import java.util.List;
import java.util.stream.Collectors;

public class EateryDataCleaner {

    public static List<GovLicensedEatingResponse.Record> clean(List<GovLicensedEatingResponse.Record> records) {
        if (records == null || records.isEmpty()) {
            System.out.println("⚠️ No records to clean (API returned empty list)");
            return List.of();
        }

        return records.stream()
                // remove records with missing essential info
                .filter(record ->
                        isValid(record.getLicenseeName()) &&
                                isValid(record.getLicenceNumber()) &&
                                isValid(record.getPremisesAddress()) &&
                                isValid(record.getGrade())
                )
                // normalize fields (trim, uppercase grade, etc.)
                .peek(EateryDataCleaner::normalize)
                .collect(Collectors.toList());
    }

    private static boolean isValid(String value) {
        return value != null && !value.trim().isEmpty() && !value.equalsIgnoreCase("na");
    }

    private static void normalize(GovLicensedEatingResponse.Record record) {
        record.setLicenseeName(record.getLicenseeName().trim());
        record.setLicenceNumber(record.getLicenceNumber().trim());
        record.setPremisesAddress(record.getPremisesAddress().trim());
        record.setGrade(record.getGrade().trim().toUpperCase());

        // Optional: Clean up "na" or empty demerit points
        if (!isValid(record.getDemeritPoints())) {
            record.setDemeritPoints("N/A");
        }
    }
}
