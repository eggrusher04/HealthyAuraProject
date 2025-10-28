package com.FeedEmGreens.HealthyAura.dto;

public class EateryDTO {
    private String licenceNumber;
    private String licenseeName;
    private String premisesAddress;
    private String grade;
    private String demeritPoints;
    private String licenceStatus;

    public String getLicenceNumber() { return licenceNumber; }
    public void setLicenceNumber(String licenceNumber) { this.licenceNumber = licenceNumber; }

    public String getLicenseeName() { return licenseeName; }
    public void setLicenseeName(String licenseeName) { this.licenseeName = licenseeName; }

    public String getPremisesAddress() { return premisesAddress; }
    public void setPremisesAddress(String premisesAddress) { this.premisesAddress = premisesAddress; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getDemeritPoints() { return demeritPoints; }
    public void setDemeritPoints(String demeritPoints) { this.demeritPoints = demeritPoints; }

    public String getLicenceStatus() { return licenceStatus; }
    public void setLicenceStatus(String licenceStatus) { this.licenceStatus = licenceStatus; }
}
