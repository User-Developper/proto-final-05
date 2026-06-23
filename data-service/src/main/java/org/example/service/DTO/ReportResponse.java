package org.example.service.DTO;

import com.medical.patient_service.model.Category;
import com.medical.patient_service.model.Gravity;

public class ReportResponse {


        private Integer id;
        private String title;
        private String description;
        private Category category;
        private Gravity gravity;
        private Double latitude;
        private Double longitude;
        private String location;







    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Gravity getGravity() {
        return gravity;
    }

    public void setGravity(Gravity gravity) {
        this.gravity = gravity;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }


    public void setLocation(String location) {
        this.location = location;
    }
    public String getLocation() {
        return location;
    }
}
