package com.jica.project;

public class showDoneList {
    String title;
    String date;
    Boolean admin_check;

    public showDoneList(String title, String date, Boolean admin_check) {
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

    public Boolean getAdmin_check() {
        return admin_check;
    }

    public void setAdmin_check(Boolean admin_check) {
        this.admin_check = admin_check;
    }

    @Override
    public String toString() {
        return "showDoneList{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", admin_check=" + admin_check +
                '}';
    }
}
