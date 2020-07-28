package com.example.eventgoapps.data.remote.model;

import com.example.eventgoapps.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id_user")
    @Expose
    private String idUser;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("nama")
    @Expose
    private String nama;

    @SerializedName("alamat")
    @Expose
    private String alamat;

    @SerializedName("foto")
    @Expose
    private String foto;

    @SerializedName("no_hp")
    @Expose
    private String noHp;

    @SerializedName("jum_like")
    @Expose
    private String jumLike;

    @SerializedName("jum_upload")
    @Expose
    private String jumUpload;

    @SerializedName("tgl_update")
    @Expose
    private String tglUpdate;

    @SerializedName("jum_sedang_proses")
    @Expose
    private String jumSedangProses;

    @SerializedName("jum_ditolak")
    @Expose
    private String jumDitolak;

    @SerializedName("jum_diterima")
    @Expose
    private String jumDiterima;

    @SerializedName("jum_notif")
    @Expose
    private String jumNotif;

    public User(String idUser, String email, String nama, String alamat, String foto, String noHp, String jumLike, String jumUpload) {
        this.idUser = idUser;
        this.email = email;
        this.nama = nama;
        this.alamat = alamat;
        this.foto = foto;
        this.noHp = noHp;
        this.jumLike = jumLike;
        this.jumUpload = jumUpload;
    }

    public User() {
    }

    public String getIdUser() {
        return idUser;
    }

    public String getEmail() {
        return email;
    }

    public String getNoHp() {
        return noHp;
    }

    public String getNama() {
        return nama;
    }

    public String getFoto() {
        String url = BuildConfig.BASE_URL_GAMBAR+"profil/";
        return url+foto;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getJumLike() {
        return jumLike;
    }

    public String getJumUpload() {
        return jumUpload;
    }

    public void setJumLike(String jumLike) {
        this.jumLike = jumLike;
    }

    public void setJumUpload(String jumUpload) {
        this.jumUpload = jumUpload;
    }

    public String getTglUpdate() {
        return tglUpdate;
    }

    public void setTglUpdate(String tglUpdate) {
        this.tglUpdate = tglUpdate;
    }

    public String getJumSedangProses() {
        return jumSedangProses;
    }

    public void setJumSedangProses(String jumSedangProses) {
        this.jumSedangProses = jumSedangProses;
    }

    public String getJumDitolak() {
        return jumDitolak;
    }

    public void setJumDitolak(String jumDitolak) {
        this.jumDitolak = jumDitolak;
    }

    public String getJumDiterima() {
        return jumDiterima;
    }

    public void setJumDiterima(String jumDiterima) {
        this.jumDiterima = jumDiterima;
    }

    public String getJumNotif() {
        return jumNotif;
    }

    public void setJumNotif(String jumNotif) {
        this.jumNotif = jumNotif;
    }
}
