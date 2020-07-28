package com.example.eventgoapps.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tb_lokasi")
public class EventLocation {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "id_event")
    private String idEvent;

    @ColumnInfo(name = "lokasi")
    private String lokasi;



    public EventLocation() {
    }

    public EventLocation(int id, String idEvent, String lokasi) {
        this.id = id;
        this.idEvent = idEvent;
        this.lokasi = lokasi;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIdEvent() {
        return idEvent;
    }

    public void setIdEvent(String idEvent) {
        this.idEvent = idEvent;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }
}
