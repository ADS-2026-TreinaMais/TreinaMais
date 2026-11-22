package com.pucgo.edu.treinamais.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity (
    foreignKeys = [ForeignKey(
        entity = Usuario_r::class,
        parentColumns = ["uid"],
        childColumns = ["usrFK"],
        onDelete = ForeignKey.CASCADE
    )
])
data class Professor (
    @PrimaryKey val profID: Long,
    val cref: String,
    val usrFK: Long
)

data class profUsuario (
    @Embedded val usr: Usuario_r,
    @Relation(parentColumn = "uid", entityColumn = "usrFK")
    val professor: Professor
)

data class alunosProf (
    @Embedded val prof: Professor,
    @Relation (parentColumn = "profID", entityColumn = "profFK")
    val alunos: List<Aluno_r>
)