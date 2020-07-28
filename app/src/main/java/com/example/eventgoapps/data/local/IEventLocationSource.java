package com.example.eventgoapps.data.local;

import com.example.eventgoapps.data.local.entity.EventLocation;
import com.example.eventgoapps.data.local.entity.LampiranEntity;

import java.util.List;

public interface IEventLocationSource {
    void insertLokasi(EventLocation eventLocation);
    void cleanLokasi();
    List<EventLocation> getAllLokasi();
}
