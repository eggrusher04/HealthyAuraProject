package com.FeedEmGreens.HealthyAura.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GovLicensedEatingResponse {

    private boolean success;
    private Result result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Result {
        private String resource_id;
        private List<Record> records;

        public String getResource_id() {
            return resource_id;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }

        public List<Record> getRecords() {
            return records;
        }

        public void setRecords(List<Record> records) {
            this.records = records;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Record {

        @JsonProperty("_id")
        private int id;

        @JsonProperty("licensee_name")
        private String licenseeName;

        @JsonProperty("licence_number")
        private String licenceNumber;

        @JsonProperty("premises_address")
        private String premisesAddress;

        @JsonProperty("grade")
        private String grade;

        @JsonProperty("demerit_points")
        private String demeritPoints;

        @JsonProperty("suspension_start_date")
        private String suspensionStartDate;

        @JsonProperty("suspension_end_date")
        private String suspensionEndDate;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLicenseeName() {
            return licenseeName;
        }

        public void setLicenseeName(String licenseeName) {
            this.licenseeName = licenseeName;
        }

        public String getLicenceNumber() {
            return licenceNumber;
        }

        public void setLicenceNumber(String licenceNumber) {
            this.licenceNumber = licenceNumber;
        }

        public String getPremisesAddress() {
            return premisesAddress;
        }

        public void setPremisesAddress(String premisesAddress) {
            this.premisesAddress = premisesAddress;
        }

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }

        public String getDemeritPoints() {
            return demeritPoints;
        }

        public void setDemeritPoints(String demeritPoints) {
            this.demeritPoints = demeritPoints;
        }

        public String getSuspensionStartDate() {
            return suspensionStartDate;
        }

        public void setSuspensionStartDate(String suspensionStartDate) {
            this.suspensionStartDate = suspensionStartDate;
        }

        public String getSuspensionEndDate() {
            return suspensionEndDate;
        }

        public void setSuspensionEndDate(String suspensionEndDate) {
            this.suspensionEndDate = suspensionEndDate;
        }
    }
}
