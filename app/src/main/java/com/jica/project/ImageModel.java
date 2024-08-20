package com.jica.project;

public class ImageModel {
    private String title;
    private String date;
    private boolean admin_check;

    public ImageModel() {
    }

    public ImageModel(String title, String date, boolean admin_check) {
        this.title = title;
        this.date = date;
        this.admin_check = admin_check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isAdmin_check() {
        return admin_check;
    }

    public void setAdmin_check(boolean admin_check) {
        this.admin_check = admin_check;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", admin_check=" + admin_check +
                '}';
    }
}

