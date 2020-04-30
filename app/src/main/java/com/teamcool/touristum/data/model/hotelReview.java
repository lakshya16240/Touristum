package com.teamcool.touristum.data.model;

public class hotelReview {
    private String HotelReviewID,ClientID,HotelID,HotelReview;

    public hotelReview(String hotelReviewID, String clientID, String hotelID, String hotelReview) {
        HotelReviewID = hotelReviewID;
        ClientID = clientID;
        HotelID = hotelID;
        HotelReview = hotelReview;
    }

    public String getHotelReviewID() {
        return HotelReviewID;
    }

    public void setHotelReviewID(String hotelReviewID) {
        HotelReviewID = hotelReviewID;
    }

    public String getClientID() {
        return ClientID;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public String getHotelID() {
        return HotelID;
    }

    public void setHotelID(String hotelID) {
        HotelID = hotelID;
    }

    public String getHotelReview() {
        return HotelReview;
    }

    public void setHotelReview(String hotelReview) {
        HotelReview = hotelReview;
    }
}
