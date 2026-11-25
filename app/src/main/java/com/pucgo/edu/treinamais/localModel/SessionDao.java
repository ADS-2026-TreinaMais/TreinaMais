package com.pucgo.edu.treinamais.localModel;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserSessionEntity session);

    @Query("SELECT * FROM UserSessionEntity LIMIT 1")
    UserSessionEntity getActiveSession();

    @Query("SELECT * FROM UserSessionEntity WHERE user_id = :userId LIMIT 1")
    UserSessionEntity getSessionByUserId(Long userId);

    @Update
    void update(UserSessionEntity session);

    @Query("DELETE FROM UserSessionEntity")
    void clearAll();
}
