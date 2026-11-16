package com.pucgo.edu.treinamais.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class Usuario (
    @PrimaryKey val uid: Long,
    val nome: String,
    val email: String,
    val dataNasc: String,
    val telefone: String,
    val criadoEm: String,
    val tipo: String,
)

