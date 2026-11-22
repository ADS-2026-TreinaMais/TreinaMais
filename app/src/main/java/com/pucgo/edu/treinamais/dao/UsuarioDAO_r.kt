package com.pucgo.edu.treinamais.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.pucgo.edu.treinamais.model.entity.Usuario_r

@Dao
interface UsuarioDAO_r {
    @Insert
    suspend fun novoUsr(usr: Usuario_r)
    @Update
    suspend fun upUsr(usr: Usuario_r)
    @Delete
    suspend fun delUsr(usr: Usuario_r)

    @Query ("SELECT * FROM usuario WHERE email = :email")
    suspend fun loginUsr(email: String): Usuario_r
}