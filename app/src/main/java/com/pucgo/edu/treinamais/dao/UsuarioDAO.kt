package com.pucgo.edu.treinamais.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pucgo.edu.treinamais.model.entity.Usuario

@Dao
interface UsuarioDAO {
    @Insert
    suspend fun novoUsr(usr: Usuario)
    @Update
    suspend fun upUsr(usr: Usuario)
    @Delete
    suspend fun delUsr(usr: Usuario)

    @Query ("SELECT * FROM usuario WHERE email = :email")
    suspend fun loginUsr(email: String): Usuario
}