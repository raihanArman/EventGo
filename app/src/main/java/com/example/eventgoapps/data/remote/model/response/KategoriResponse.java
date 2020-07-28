package com.example.eventgoapps.data.remote.model.response;

import com.example.eventgoapps.data.remote.model.Kategori;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class KategoriResponse {
    @SerializedName("value")
    @Expose
    private int value;

    @SerializedName("message")
    @Expose
    private String message;

    @SerializedName("data_kategori")
    @Expose
    private List<Kategori> kategoriList;

    public KategoriResponse(int value, String message, List<Kategori> kategoriList) {
        this.value = value;
        this.message = message;
        this.kategoriList = kategoriList;
    }

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public List<Kategori> getKategoriList() {
        return kategoriList;
    }
}
