package com.teamcool.touristum.data.model;

public class DriverTrip {

    String fromDate, days, nights, cityname, cityID, clientName, clientID, clientContact;

    public DriverTrip(String fromDate, String days, String nights, String cityname, String cityID, String clientName, String clientID, String clientContact) {
        this.fromDate = fromDate;
        this.days = days;
        this.nights = nights;
        this.cityname = cityname;
        this.cityID = cityID;
        this.clientName = clientName;
        this.clientID = clientID;
        this.clientContact = clientContact;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getNights() {
        return nights;
    }

    public void setNights(String nights) {
        this.nights = nights;
    }

    public String getCityname() {
        return cityname;
    }

    public void setCityname(String cityname) {
        this.cityname = cityname;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getClientContact() {
        return clientContact;
    }

    public void setClientContact(String clientContact) {
        this.clientContact = clientContact;
    }
}
