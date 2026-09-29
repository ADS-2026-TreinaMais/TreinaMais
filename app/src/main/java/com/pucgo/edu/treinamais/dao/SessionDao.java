package com.pucgo.edu.treinamais.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.pucgo.edu.treinamais.model.entity.UserSessionEntity;

@Dao
public interface SessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UserSessionEntity session);

    @Query("SELECT * FROM user_sessions LIMIT 1")
    UserSessionEntity getActiveSession();

    @Query("SELECT * FROM user_sessions WHERE user_id = :userId LIMIT 1")
    UserSessionEntity getSessionByUserId(Long userId);

    @Update
    void update(UserSessionEntity session);

    @Query("DELETE FROM user_sessions")
    void clearAll();
}
