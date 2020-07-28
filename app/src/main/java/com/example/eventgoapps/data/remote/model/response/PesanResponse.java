package com.example.eventgoapps.data.remote.model.response;

import com.example.eventgoapps.data.remote.model.Pesan;
import com.example.eventgoapps.data.remote.model.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PesanResponse {
    @SerializedName("value")
    @Expose
    private int value;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data_pesan")
    @Expose
    private List<Pesan> pesanList;

    public PesanResponse(int value, String message, List<Pesan> pesanList) {
        this.value = value;
        this.message = message;
        this.pesanList = pesanList;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Pesan> getPesanList() {
        return pesanList;
    }

    public void setPesanList(List<Pesan> pesanList) {
        this.pesanList = pesanList;
    }
}
