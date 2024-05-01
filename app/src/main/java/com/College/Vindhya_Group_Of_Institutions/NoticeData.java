package com.College.Vindhya_Group_Of_Institutions;

public class NoticeData {

    // Attributes
    private String title;
    private String date;
    private String time;
    private String image;
    private String key;


    // Constructors
    public NoticeData() {
    }

    public NoticeData(String title, String date, String time, String image, String key) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.image = image;
        this.key = key;
    }


    // Getters and setters
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
