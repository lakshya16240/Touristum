package com.teamcool.touristum.data.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Chat implements Serializable {

    String clientID, ClientName, employeeID, employeeName;
    ArrayList<Message> messages;

    public Chat(String clientID, String clientName, String employeeID, String employeeName, ArrayList<Message> messages) {
        this.clientID = clientID;
        ClientName = clientName;
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.messages = messages;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public void setMessages(ArrayList<Message> messages) {
        this.messages = messages;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }
}
