package com.pucgo.edu.treinamais.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    foreignKeys = [ForeignKey(
        entity = Usuario_r::class,
        parentColumns = ["uid"],
        childColumns = ["usrFK"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Professor::class,
        parentColumns = ["profID"],
        childColumns = ["profFK"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Aluno_r (
    @PrimaryKey val alunoID: Long,
    val usrFK: Long,
    val profFK: Long
)
data class alunoUsuario (
    @Embedded val usr: Usuario_r,
    @Relation(parentColumn = "uid", entityColumn = "usrFK")
    val alunoUsuario: Aluno_r
)

data class alunoProf (
    @Embedded val prof: Professor,
    @Relation(parentColumn = "profID", entityColumn = "profFK")
    val alunosDoProf: List<Aluno_r>
)