package com.example.eventgoapps.data.remote.model.response;

import com.example.eventgoapps.data.remote.model.Event;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventResponse {
    @SerializedName("value")
    @Expose
    private int value;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data_event")
    @Expose
    private List<Event> eventList;

    public EventResponse(int value, String message, List<Event> eventList) {
        this.value = value;
        this.message = message;
        this.eventList = eventList;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<Event> getEventList() {
        return eventList;
    }
}
