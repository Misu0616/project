package com.jica.project;

public class ActivityModel {
    private String activityName;

    public ActivityModel() {
        // Default constructor required for calls to DataSnapshot.getValue(ActivityModel.class)
    }

    public ActivityModel(String activityName) {
        this.activityName = activityName;
    }

    public String getActivityName() {
        return activityName;
    }
}
