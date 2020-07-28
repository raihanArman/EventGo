package com.example.eventgoapps.data.remote.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.eventgoapps.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

public class Event implements Parcelable {
    @SerializedName("id_event")
    @Expose
    private String idEvent;

    @SerializedName("id_kategori")
    @Expose
    private String idKategori;

    @SerializedName("id_user")
    @Expose
    private String idUser;

    @SerializedName("pamflet")
    @Expose
    private String pamflet;

    @SerializedName("judul")
    @Expose
    private String judul;

    @SerializedName("sub_judul")
    @Expose
    private String subJudul;

    @SerializedName("deskripsi")
    @Expose
    private String deskripsi;

    @SerializedName("lokasi")
    @Expose
    private String lokasi;

    @SerializedName("no_hp_panitia")
    @Expose
    private String noHpPanitia;

    @SerializedName("nama_panitia")
    @Expose
    private String namaPanitia;

    @SerializedName("tgl_upload")
    @Expose
    private Date tglUpload;

    @SerializedName("tgl_event")
    @Expose
    private Date tglEvent;

    @SerializedName("nama_kategori")
    @Expose
    private String namaKategori;

    @SerializedName("nama")
    @Expose
    private String namaUser;

    @SerializedName("jumlah_like")
    @Expose
    private String jumlahLike;

    @SerializedName("jenis")
    @Expose
    private String jenis;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("tanggal")
    @Expose
    private String tanggal;

    @SerializedName("tgl_update")
    @Expose
    private String tglUpdate;

    @SerializedName("tgl_riwayat")
    @Expose
    private Date tglRiwayat;

    @SerializedName("kategori")
    @Expose
    private String kategori;

    @SerializedName("id_riwayat_user")
    @Expose
    private String idRiwayat;

    private String pamfletUrl;

    public Event() {
    }

    public Event(String idEvent, String idKategori, String idUser, String pamflet, String judul, String subJudul, String deskripsi, String lokasi, String noHpPanitia, Date tglUpload, Date tglEvent, String namaKategori, String namaUser, String jumlahLike, String jenis, String status) {
        this.idEvent = idEvent;
        this.idKategori = idKategori;
        this.idUser = idUser;
        this.pamflet = pamflet;
        this.judul = judul;
        this.subJudul = subJudul;
        this.deskripsi = deskripsi;
        this.lokasi = lokasi;
        this.noHpPanitia = noHpPanitia;
        this.tglUpload = tglUpload;
        this.tglEvent = tglEvent;
        this.namaKategori = namaKategori;
        this.namaUser = namaUser;
        this.jumlahLike = jumlahLike;
        this.jenis = jenis;
        this.status = status;
    }

    public Date getTglRiwayat() {
        return tglRiwayat;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public void setTglRiwayat(Date tglRiwayat) {
        this.tglRiwayat = tglRiwayat;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public String getIdKategori() {
        return idKategori;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getPamflet() {
        String url = BuildConfig.BASE_URL_GAMBAR+"event/";
        return url+pamflet;
    }

    public String getPamfletUrl() {
        return pamfletUrl;
    }

    public void setPamfletUrl(String pamfletUrl) {
        this.pamfletUrl = pamfletUrl;
    }

    public String getJudul() {
        return judul;
    }

    public String getSubJudul() {
        return subJudul;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public String getLokasi() {
        return lokasi;
    }

    public String getNoHpPanitia() {
        return noHpPanitia;
    }

    public Date getTglUpload() {
        return tglUpload;
    }

    public Date getTglEvent() {
        return tglEvent;
    }

    public String getNamaKategori() {
        return namaKategori;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public String getJumlahLike() {
        return jumlahLike;
    }

    public String getJenis() {
        return jenis;
    }

    public String getStatus() {
        return status;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public void setIdKategori(String idKategori) {
        this.idKategori = idKategori;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public void setPamflet(String pamflet) {
        this.pamflet = pamflet;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public void setSubJudul(String subJudul) {
        this.subJudul = subJudul;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public void setNoHpPanitia(String noHpPanitia) {
        this.noHpPanitia = noHpPanitia;
    }

    public void setTglUpload(Date tglUpload) {
        this.tglUpload = tglUpload;
    }

    public void setTglEvent(Date tglEvent) {
        this.tglEvent = tglEvent;
    }

    public void setNamaKategori(String namaKategori) {
        this.namaKategori = namaKategori;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public void setJumlahLike(String jumlahLike) {
        this.jumlahLike = jumlahLike;
    }

    public void setJenis(String jenis) {
        this.jenis = jenis;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNamaPanitia() {
        return namaPanitia;
    }

    public void setNamaPanitia(String namaPanitia) {
        this.namaPanitia = namaPanitia;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getIdRiwayat() {
        return idRiwayat;
    }

    public void setIdRiwayat(String idRiwayat) {
        this.idRiwayat = idRiwayat;
    }

    public String getTglUpdate() {
        return tglUpdate;
    }

    public void setTglUpdate(String tglUpdate) {
        this.tglUpdate = tglUpdate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.idEvent);
        dest.writeString(this.idKategori);
        dest.writeString(this.idUser);
        dest.writeString(this.pamflet);
        dest.writeString(this.judul);
        dest.writeString(this.subJudul);
        dest.writeString(this.deskripsi);
        dest.writeString(this.lokasi);
        dest.writeString(this.noHpPanitia);
        dest.writeString(this.namaPanitia);
        dest.writeLong(this.tglUpload != null ? this.tglUpload.getTime() : -1);
        dest.writeLong(this.tglEvent != null ? this.tglEvent.getTime() : -1);
        dest.writeString(this.namaKategori);
        dest.writeString(this.namaUser);
        dest.writeString(this.jumlahLike);
        dest.writeString(this.jenis);
        dest.writeString(this.status);
        dest.writeString(this.tanggal);
    }

    protected Event(Parcel in) {
        this.idEvent = in.readString();
        this.idKategori = in.readString();
        this.idUser = in.readString();
        this.pamflet = in.readString();
        this.judul = in.readString();
        this.subJudul = in.readString();
        this.deskripsi = in.readString();
        this.lokasi = in.readString();
        this.noHpPanitia = in.readString();
        this.namaPanitia = in.readString();
        long tmpTglUpload = in.readLong();
        this.tglUpload = tmpTglUpload == -1 ? null : new Date(tmpTglUpload);
        long tmpTglEvent = in.readLong();
        this.tglEvent = tmpTglEvent == -1 ? null : new Date(tmpTglEvent);
        this.namaKategori = in.readString();
        this.namaUser = in.readString();
        this.jumlahLike = in.readString();
        this.jenis = in.readString();
        this.status = in.readString();
        this.tanggal = in.readString();
    }

    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>() {
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }

        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
