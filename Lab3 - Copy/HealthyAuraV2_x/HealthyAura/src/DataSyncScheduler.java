package com.FeedEmGreens.HealthyAura.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class DataSyncScheduler {

    private final HawkerCenterApiService hawkerCenterApiService;

    @Autowired
    public DataSyncScheduler(HawkerCenterApiService hawkerCenterApiService) {
        this.hawkerCenterApiService = hawkerCenterApiService;
    }

    // Sync data every 24 hours at 2 AM
    @Scheduled(cron = "0 0 2 * * ?")
    public void syncHawkerCenterData() {
        System.out.println("Starting scheduled hawker center data sync...");
        hawkerCenterApiService.syncData();
        System.out.println("Scheduled sync completed.");
    }

    // Manual sync method
    public void manualSync() {
        hawkerCenterApiService.syncData();
    }
}
