package com.example.eventgoapps.data.remote.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Pesan {
    @SerializedName("id_pesan_user")
    @Expose
    private String idPesanUser;

    @SerializedName("id_event")
    @Expose
    private String idevent;

    @SerializedName("id_user")
    @Expose
    private String iduser;

    @SerializedName("pesan")
    @Expose
    private String pesan;

    @SerializedName("tanggal")
    @Expose
    private Date tanggal;

    @SerializedName("status")
    @Expose
    private String status;

    public Pesan() {
    }

    public Pesan(String idevent, String iduser, String pesan, Date tanggal) {
        this.idevent = idevent;
        this.iduser = iduser;
        this.pesan = pesan;
        this.tanggal = tanggal;
    }

    public String getIdevent() {
        return idevent;
    }

    public void setIdevent(String idevent) {
        this.idevent = idevent;
    }

    public String getIduser() {
        return iduser;
    }

    public void setIduser(String iduser) {
        this.iduser = iduser;
    }

    public String getPesan() {
        return pesan;
    }

    public void setPesan(String pesan) {
        this.pesan = pesan;
    }

    public Date getTanggal() {
        return tanggal;
    }

    public void setTanggal(Date tanggal) {
        this.tanggal = tanggal;
    }

    public String getIdPesanUser() {
        return idPesanUser;
    }

    public void setIdPesanUser(String idPesanUser) {
        this.idPesanUser = idPesanUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
