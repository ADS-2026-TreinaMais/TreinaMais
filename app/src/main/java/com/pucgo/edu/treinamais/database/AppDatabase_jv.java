package com.pucgo.edu.treinamais.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.pucgo.edu.treinamais.dao.SessionDao;
import com.pucgo.edu.treinamais.model.entity.UserSessionEntity;

@Database(entities = {UserSessionEntity.class}, version = 1, exportSchema = false)
public abstract class AppDatabase_jv extends RoomDatabase {

    private static volatile AppDatabase_jv INSTANCE;

    public abstract SessionDao sessionDAO();

    public static AppDatabase_jv getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase_jv.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase_jv.class,
                            "treinamais_app.db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}
