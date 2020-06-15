package com.example.autofitgridview;

public class DataModel {


    private String image;
    private String title;
    private String desc;

    public DataModel(String image, String title, String desc) {

        this.image = image;
        this.title = title;
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }
}
