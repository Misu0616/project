package com.jica.project;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageModel {
    private String title;
    private String date;
    private boolean admin_check;
    private String imgURL;

    public ImageModel() {
    }
    public ImageModel(String imgURL) {
        this.imgURL = imgURL;
    }

    public ImageModel(String title, String date, boolean admin_check, String imgURL) {
        this.title = title;
        this.date = date;
        this.admin_check = admin_check;
        this.imgURL = imgURL;
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

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "title='" + title + '\'' +
                ", date='" + date + '\'' +
                ", admin_check=" + admin_check +'\'' +
                "imgURL='" + imgURL + '\'' +
                '}';
    }
}

