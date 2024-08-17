package com.jica.project;

public class ActivityModel {
    private String actNumber;

    public ActivityModel() {
        // 기본 생성자 필요
    }

    public ActivityModel(String actNumber) {
        this.actNumber = actNumber;
    }

    public String getActNumber() {
        return actNumber;
    }

    public void setActNumber(String actNumber) {
        this.actNumber = actNumber;
    }

    @Override
    public String toString() {
        return "ActivityModel{" +
                "actNumber='" + actNumber + '\'' +
                '}';
    }
}

