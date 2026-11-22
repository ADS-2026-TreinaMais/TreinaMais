package com.pucgo.edu.treinamais.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pucgo.edu.treinamais.dao.AlunoDAO_r
import com.pucgo.edu.treinamais.dao.ProfessorDAO_r
import com.pucgo.edu.treinamais.dao.SessionDao
import com.pucgo.edu.treinamais.model.Aluno
import com.pucgo.edu.treinamais.model.entity.ExercTreino_r
import com.pucgo.edu.treinamais.model.entity.Professor
import com.pucgo.edu.treinamais.model.entity.Treino

//@Database(entities = [Aluno::class, Professor::class, Treino::class, ExercTreino_r::class], version = 1)
abstract class AppDatabaseR : RoomDatabase() {
    abstract fun alunoDAO(): AlunoDAO_r
    abstract fun profDAO(): ProfessorDAO_r
    abstract fun sessionDAO(): SessionDao

    companion object {
        @Volatile
        private var db: AppDatabaseR? = null

        @JvmStatic
        fun getDB(cx: Context): AppDatabaseR =
            db ?: synchronized(this) {
                Room.databaseBuilder(
                    cx.applicationContext,
                    AppDatabaseR::class.java,
                    "TMDB"
                ).build().also { db = it }
            }
    }
}
