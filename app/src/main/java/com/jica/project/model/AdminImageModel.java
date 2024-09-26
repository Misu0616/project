package com.jica.project.model;

public class AdminImageModel {
    private String title;
    private String date;
    private boolean admin_check;
    private String downloadurl;
    private String userID;
    private String documentId;


    public AdminImageModel() {
    }
    public AdminImageModel(String documentId, String title, String date, boolean admin_check, String downloadurl, String userID) {
        this.documentId = documentId;
        this.title = title;
        this.date = date;
        this.admin_check = admin_check;
        this.downloadurl = downloadurl;
        this.userID = userID;

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

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", admin_check=" + admin_check +'\'' +
                ", downloadurl=" + downloadurl +'\'' +
                ", userID=" + userID +'\'' +
                '}';
    }
}

