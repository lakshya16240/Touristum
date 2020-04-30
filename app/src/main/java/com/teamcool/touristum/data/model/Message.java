package com.teamcool.touristum.data.model;

import java.io.Serializable;

public class Message implements Serializable {

    String type, message, name;

    public Message(String type, String message, String id) {
        this.type = type;
        this.message = message;
        this.name = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public void setName(String id) {
        this.name = id;
    }
}
