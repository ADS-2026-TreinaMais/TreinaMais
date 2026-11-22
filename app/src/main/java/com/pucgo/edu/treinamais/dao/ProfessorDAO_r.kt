package com.pucgo.edu.treinamais.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.pucgo.edu.treinamais.model.entity.Professor
import com.pucgo.edu.treinamais.model.entity.alunosProf
import com.pucgo.edu.treinamais.model.entity.profUsuario

@Dao
interface ProfessorDAO_r {
    @Insert
    suspend fun inserirProf(prof: Professor)

    @Query("SELECT * FROM professor WHERE profID = :pk")
    suspend fun selcProf(pk: Long): Professor

    @Transaction
    @Query("SELECT * FROM professor WHERE usrFK = :uid")
    suspend fun selcProfUsuario(uid: Long): profUsuario

    @Transaction
    @Query ("SELECT * FROM Aluno")
    suspend fun selcAlunos(): List<alunosProf>

    @Delete
    suspend fun delProf(prof: Professor)
}