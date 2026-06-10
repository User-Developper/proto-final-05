package com.medical.web_app.service.dto;

import com.medical.web_app.model.Category;
import com.medical.web_app.model.Gravity;

public class CreateReportTemp {

    private String title;
    private String description;
    private String location;

    private Double latitude;
    private Double longitude;

    private Category category;
    private Gravity gravity;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Gravity getGravity() { return gravity; }
    public void setGravity(Gravity gravity) { this.gravity = gravity; }
}
