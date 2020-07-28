package com.example.eventgoapps.data.local;

import android.app.Application;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.eventgoapps.data.local.dao.DatabaseDao;
import com.example.eventgoapps.data.local.entity.EventLocation;
import com.example.eventgoapps.data.local.entity.LampiranEntity;

@Database(entities = {LampiranEntity.class, EventLocation.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract DatabaseDao databaseDao();

    public static AppDatabase getAppDatabase(Application application){
        return Room.databaseBuilder(application, AppDatabase.class, "db_event")
                .allowMainThreadQueries().build();
    }

}
