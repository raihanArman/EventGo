package com.example.eventgoapps.data.remote.model.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.eventgoapps.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class EventTerdekat {
    @SerializedName("id_event")
    @Expose
    private String idEvent;

    @SerializedName("id_kategori")
    @Expose
    private String idKategori;

    @SerializedName("pamflet")
    @Expose
    private String pamflet;

    @SerializedName("judul")
    @Expose
    private String judul;

    @SerializedName("sub_judul")
    @Expose
    private String subJudul;

    @SerializedName("lokasi")
    @Expose
    private String lokasi;

    @SerializedName("tgl_event")
    @Expose
    private Date tglEvent;


    public EventTerdekat() {
    }

    public EventTerdekat(String idEvent, String idKategori, String pamflet, String judul, String subJudul, String lokasi, Date tglEvent) {
        this.idEvent = idEvent;
        this.idKategori = idKategori;
        this.pamflet = pamflet;
        this.judul = judul;
        this.subJudul = subJudul;
        this.lokasi = lokasi;
        this.tglEvent = tglEvent;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public String getPamflet() {
        return pamflet;
    }

    public void setPamflet(String pamflet) {
        this.pamflet = pamflet;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public String getSubJudul() {
        return subJudul;
    }

    public void setSubJudul(String subJudul) {
        this.subJudul = subJudul;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public Date getTglEvent() {
        return tglEvent;
    }

    public void setTglEvent(Date tglEvent) {
        this.tglEvent = tglEvent;
    }
}
