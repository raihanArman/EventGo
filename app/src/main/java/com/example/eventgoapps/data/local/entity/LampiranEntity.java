package com.example.eventgoapps.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_lampiran")
public class LampiranEntity {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "id_user")
    private String idUser;

    @ColumnInfo(name = "gambar")
    private String gambar;

    @ColumnInfo(name = "keterangan")
    private String keterangan;

    public LampiranEntity() {
    }

    public LampiranEntity(int id, String idUser, String gambar, String keterangan) {
        this.id = id;
        this.idUser = idUser;
        this.gambar = gambar;
        this.keterangan = keterangan;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
}
