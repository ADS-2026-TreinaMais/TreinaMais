package com.pucgo.edu.treinamais.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.pucgo.edu.treinamais.model.entity.Professor

@Dao
interface ProfessorDAO {
    @Insert
    suspend fun inserirProf(prof: Professor)

    @Query("SELECT * FROM professor WHERE profID = :pk")
    suspend fun selcProf(pk: Long): Professor

    @Delete
    suspend fun delProf(prof: Professor)
}