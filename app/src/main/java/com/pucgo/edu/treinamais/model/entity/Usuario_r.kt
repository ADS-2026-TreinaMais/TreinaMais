package com.pucgo.edu.treinamais.model.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["email"], unique = true)])
data class Usuario_r (
    @PrimaryKey val uid: Long,
    val cpf: String,
    val nome: String,
    val email: String,
    val dataNasc: String,
    val telefone: String,
    val criadoEm: String,
    val tipo: String
)

