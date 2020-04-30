package com.teamcool.touristum.data.model;

import java.io.Serializable;

public class TouristCity implements Serializable {

    private String cityID,cityName,rating,noOfLocations;

    public TouristCity(String cityID, String cityName, String rating, String noOfLocations) {
        this.cityID = cityID;
        this.cityName = cityName;
        this.rating = rating;
        this.noOfLocations = noOfLocations;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getNoOfLocations() {
        return noOfLocations;
    }

    public void setNoOfLocations(String noOfLocations) {
        this.noOfLocations = noOfLocations;
    }
}
