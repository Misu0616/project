package com.jica.project;

public class urlModel {
    private String imgURL;

    public urlModel() {
    }
    public urlModel(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    @Override
    public String toString() {
        return "urlModel{" +
                "imgURL='" + imgURL + '\'' +
                '}';
    }
}

