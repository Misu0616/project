package com.jica.project;

public class ImagePicModel {
    private String imgURL;

    public ImagePicModel() {
    }

    public ImagePicModel(String imgURL) {
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
        return "ImagePicModel{" +
                "imgURL='" + imgURL + '\'' +
                '}';
    }
}

