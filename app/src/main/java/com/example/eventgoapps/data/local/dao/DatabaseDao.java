package com.example.eventgoapps.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.eventgoapps.data.local.entity.EventLocation;
import com.example.eventgoapps.data.local.entity.LampiranEntity;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface DatabaseDao {
    @Insert
    void insertLampiran(LampiranEntity lampiranEntity);

    @Query("SELECT * FROM tb_lampiran WHERE id_user = :idUser")
    List<LampiranEntity> getAllLamprian(String idUser);

    @Query("DELETE FROM tb_lampiran WHERE id = :id AND id_user = :idUser")
    void removeLampiranById(int id, String idUser);

    @Query("DELETE FROM tb_lampiran WHERE id_user = :idUser")
    void cleanLampiran(String idUser);

    @Insert
    void insertLokasi(EventLocation eventLocation);

    @Query("SELECT * FROM tb_lokasi")
    List<EventLocation> getAllLokasiEvent();

    @Query("DELETE FROM tb_lokasi")
    void cleanLokasi();


}
