package com.pucgo.edu.treinamais.model.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    foreignKeys = [ForeignKey(
        entity = Professor::class,
        parentColumns = ["profID"],
        childColumns = ["profFK"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Treino (
    @PrimaryKey
    val tID: Long,
    val nome: String,
    val desc: String,
    val dataCriacao: String,
    val profFK: Long
)

//Entidade associativa alunos-treinos
@Entity(
    primaryKeys = ["alunoFK", "trFK"],
    indices = [Index("alunoFK", "trFK")],
    foreignKeys = [ForeignKey(
        entity = Aluno::class,
        parentColumns = ["alunoID"],
        childColumns = ["alunoFK"],
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Treino::class,
        parentColumns = ["tID"],
        childColumns = ["tFK"],
        onDelete = ForeignKey.CASCADE
    )])
data class alunoTreino (
    val alunoFK: Long,
    val tFK: Long
)

data class treinosDoAluno (
    @Embedded
    val aluno: Aluno,
    @Relation(
        parentColumn = "alunoID",
        entityColumn = "tID",
        associateBy = Junction(alunoTreino::class)
    ) val treinos: List<Treino>
)
