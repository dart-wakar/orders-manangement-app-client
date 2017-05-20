package com.wakarkhan.deliverydrop.model;

/**
 * Created by wakar on 19/5/17.
 */

public class Order {

    private String title;
    private String website_name;
    private int status;
    private int id;

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }

    public String getWebsite_name() {
        return website_name;
    }

    public int getStatus() {
        return status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setWebsite_name(String website_name) {
        this.website_name = website_name;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
