package com.pucgo.edu.treinamais.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.sql.Date

@Entity(
    foreignKeys = [ForeignKey(
        entity = Professor::class,
        parentColumns = ["profID"],
        childColumns = ["profFK"],
        onDelete = ForeignKey.SET_NULL
    ), ForeignKey(
        entity = Usuario::class,
        parentColumns = ["uid"],
        childColumns = ["usrFK"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Aluno (
    @PrimaryKey val alunoID: Long,
    val cpf: String,
    val dataNasc: Date,
    val telefone: String,
    val usrFK: Long,
    val profFK: Long,
    val treinoFK: Long
)
data class alunoUsuario (
    @Embedded val usr: Usuario,
    @Relation(parentColumn = "uid", entityColumn = "usrFK")
    val alunoUsuario: Aluno
)

data class alunoProf (
    @Embedded val prof: Professor,
    @Relation(parentColumn = "profID", entityColumn = "profFK")
    val alunosDoProf: List<Aluno>
)