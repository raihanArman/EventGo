package com.example.eventgoapps.data.remote.model.response;

import com.example.eventgoapps.data.remote.model.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class UserResponse {
    @SerializedName("value")
    @Expose
    private int value;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("user")
    @Expose
    private User user;

    public UserResponse(int value, String message, User user) {
        this.value = value;
        this.message = message;
        this.user = user;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}
