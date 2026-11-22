package com.pucgo.edu.treinamais.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.pucgo.edu.treinamais.model.Aluno
import com.pucgo.edu.treinamais.model.entity.Professor
import com.pucgo.edu.treinamais.model.entity.Usuario_r
import com.pucgo.edu.treinamais.model.entity.alunoUsuario
import com.pucgo.edu.treinamais.model.entity.treinosDoAluno

@Dao
interface AlunoDAO_r {
    @Insert
    suspend fun insert(aluno: Aluno)

    @Query("SELECT * FROM Aluno WHERE alunoID = :pk")
    suspend fun selcAlunoPK(pk: Long): Aluno

    @Transaction
    @Query("SELECT * FROM Professor WHERE profID = :profFK")
    suspend fun selcProfAluno(profFK: Long): Professor

    //@Query("SELECT * FROM Aluno INNER JOIN Usuario ON Aluno.usrFK = Usuario.uid")
    @Transaction
    @Query("SELECT * FROM aluno")
    suspend fun selcAlunoDoUsuario(usr: Usuario_r): alunoUsuario

    @Transaction
    @Query("SELECT * FROM treino")
    suspend fun selcTreinosDoAluno(aluno: Aluno): List<treinosDoAluno>

    @Delete
    suspend fun delAluno(aluno: Aluno)
}