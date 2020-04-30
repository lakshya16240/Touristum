package com.teamcool.touristum.data.model;

import java.util.ArrayList;

public class Client {

    private String ClientID,ClientName,ClientContact,ClientAddress,ClientEmail,NoOfBookings;
    private ArrayList<Booking> bookings;

    public Client(String clientID, String clientName, String clientContact, String clientAddress, String clientEmail, String noOfBookings) {
        ClientID = clientID;
        ClientName = clientName;
        ClientContact = clientContact;
        ClientAddress = clientAddress;
        ClientEmail = clientEmail;
        NoOfBookings = noOfBookings;
    }

    public String getClientID() {
        return ClientID;
    }

    public String getClientName() {
        return ClientName;
    }

    public String getClientContact() {
        return ClientContact;
    }

    public String getClientAddress() {
        return ClientAddress;
    }

    public String getClientEmail() {
        return ClientEmail;
    }

    public String getNoOfBookings() {
        return NoOfBookings;
    }

    public ArrayList<Booking> getBookings() {
        return bookings;
    }

    public void setClientID(String clientID) {
        ClientID = clientID;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public void setClientContact(String clientContact) {
        ClientContact = clientContact;
    }

    public void setClientAddress(String clientAddress) {
        ClientAddress = clientAddress;
    }

    public void setClientEmail(String clientEmail) {
        ClientEmail = clientEmail;
    }

    public void setNoOfBookings(String noOfBookings) {
        NoOfBookings = noOfBookings;
    }

    public void setBookings(ArrayList<Booking> bookings) {
        this.bookings = bookings;
    }
}
