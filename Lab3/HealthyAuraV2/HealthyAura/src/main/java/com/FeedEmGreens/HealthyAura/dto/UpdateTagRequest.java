package com.FeedEmGreens.HealthyAura.dto;

public class UpdateTagRequest {
    private String oldTag;
    private String newTag;

    public String getOldTag() {
        return oldTag;
    }

    public void setOldTag(String oldTag) {
        this.oldTag = oldTag;
    }

    public String getNewTag() {
        return newTag;
    }

    public void setNewTag(String newTag) {
        this.newTag = newTag;
    }
}


