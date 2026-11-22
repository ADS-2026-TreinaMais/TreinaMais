package com.pucgo.edu.treinamais.model.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [ForeignKey(
        entity = Treino::class,
        parentColumns = ["tID"],
        childColumns = ["trFK"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class ExercTreino_r(
    @PrimaryKey
    val exID: Long,
    val nomeex: String,
    val series: Int,
    val rep: Int,
    val carga: Float,
    val ordem: Int,
    val trFK: Long
)