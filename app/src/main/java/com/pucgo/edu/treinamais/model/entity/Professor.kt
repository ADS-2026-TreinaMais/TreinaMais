package com.pucgo.edu.treinamais.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation


data class Professor (
    @PrimaryKey val profID: Long,
    val cref: String,
    val usrFK: Long,
)

data class profUsuario (
    @Embedded val usr: Usuario,
    @Relation(parentColumn = "uid", entityColumn = "usrFK")
    val professor: Professor
)