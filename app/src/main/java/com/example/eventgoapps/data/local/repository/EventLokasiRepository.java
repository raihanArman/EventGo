package com.example.eventgoapps.data.local.repository;

import android.app.Application;

import com.example.eventgoapps.data.local.AppDatabase;
import com.example.eventgoapps.data.local.IEventLocationSource;
import com.example.eventgoapps.data.local.dao.DatabaseDao;
import com.example.eventgoapps.data.local.entity.EventLocation;

import java.util.List;

public class EventLokasiRepository implements IEventLocationSource {
    private DatabaseDao databaseDao;

    public EventLokasiRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getAppDatabase(application);
        databaseDao = appDatabase.databaseDao();
    }

    @Override
    public void insertLokasi(EventLocation eventLocation) {
        databaseDao.insertLokasi(eventLocation);
    }

    @Override
    public void cleanLokasi() {
        databaseDao.cleanLokasi();
    }

    @Override
    public List<EventLocation> getAllLokasi() {
        return databaseDao.getAllLokasiEvent();
    }
}
