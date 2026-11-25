package com.pucgo.edu.treinamais.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(indices = [Index(value = ["email"], unique = true)])
data class Usuario (
    @PrimaryKey val uid: Long,
    val cpf: String,
    val nome: String,
    val email: String,
    val dataNasc: String,
    val telefone: String,
    val criadoEm: String,
    val tipo: String
)

