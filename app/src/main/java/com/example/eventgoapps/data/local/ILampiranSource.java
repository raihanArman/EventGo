package com.example.eventgoapps.data.local;

import com.example.eventgoapps.data.local.entity.LampiranEntity;

import java.util.List;

public interface ILampiranSource {
    void insertLampiran(LampiranEntity lampiranEntity);
    void removeLampiranById(int id, String idUser);
    void cleanLampiran(String idUser);
    List<LampiranEntity> getAllLamprian(String idUser);
}
