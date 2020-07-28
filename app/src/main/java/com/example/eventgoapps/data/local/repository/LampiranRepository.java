package com.example.eventgoapps.data.local.repository;

import android.app.Application;

import com.example.eventgoapps.data.local.AppDatabase;
import com.example.eventgoapps.data.local.ILampiranSource;
import com.example.eventgoapps.data.local.dao.DatabaseDao;
import com.example.eventgoapps.data.local.entity.LampiranEntity;

import java.util.List;

public class LampiranRepository implements ILampiranSource {
    private DatabaseDao databaseDao;
    public LampiranRepository(Application application){
        AppDatabase appDatabase = AppDatabase.getAppDatabase(application);
        databaseDao = appDatabase.databaseDao();
    }


    @Override
    public void insertLampiran(LampiranEntity lampiranEntity) {
        databaseDao.insertLampiran(lampiranEntity);
    }

    @Override
    public void removeLampiranById(int id, String idUser) {
        databaseDao.removeLampiranById(id, idUser);
    }

    @Override
    public void cleanLampiran(String idUser) {
        databaseDao.cleanLampiran(idUser);
    }

    @Override
    public List<LampiranEntity> getAllLamprian(String idUser) {
        return databaseDao.getAllLamprian(idUser);
    }
}
