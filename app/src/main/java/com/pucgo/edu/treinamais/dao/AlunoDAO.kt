package com.pucgo.edu.treinamais.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.pucgo.edu.treinamais.model.entity.Aluno
import com.pucgo.edu.treinamais.model.entity.Usuario

@Dao
interface AlunoDAO {
    @Insert
    suspend fun insert(aluno: Aluno)

    @Query("SELECT * FROM Aluno WHERE alunoID = :pk")
    suspend fun selcAlunoPK(pk: Long): Aluno

    @Query("SELECT * FROM Aluno INNER JOIN Usuario ON Aluno.usrFK = Usuario.uid")
    suspend fun selcAlunoDoUsuario(usr: Usuario): Aluno

    @Delete
    suspend fun delAluno(aluno: Aluno)
}