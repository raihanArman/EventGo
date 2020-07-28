package com.example.eventgoapps.data.remote.model.response;

import com.example.eventgoapps.data.remote.model.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class LikeEventResponse {
    @SerializedName("value")
    @Expose
    private int value;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data_like_event")
    @Expose
    private List<User> userList;

    public LikeEventResponse(int value, String message, List<User> userList) {
        this.value = value;
        this.message = message;
        this.userList = userList;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<User> getUserList() {
        return userList;
    }
}
