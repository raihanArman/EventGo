package com.example.eventgoapps.data.remote.model;

import com.example.eventgoapps.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Kategori {
    @SerializedName("id_kategori")
    @Expose
    private String idKategori;

    @SerializedName("nama_kategori")
    @Expose
    private String namaKategori;

    @SerializedName("gambar")
    @Expose
    private String gambar;

    @SerializedName("jum_kategori")
    @Expose
    private String jumKategori;

    public Kategori() {
    }

    public Kategori(String idKategori, String namaKategori, String gambar) {
        this.idKategori = idKategori;
        this.namaKategori = namaKategori;
        this.gambar = gambar;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public String getGambar() {
        String url = BuildConfig.BASE_URL_GAMBAR+"kategori/";
        return url+gambar;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getJumKategori() {
        return jumKategori;
    }

    public void setJumKategori(String jumKategori) {
        this.jumKategori = jumKategori;
    }
}
