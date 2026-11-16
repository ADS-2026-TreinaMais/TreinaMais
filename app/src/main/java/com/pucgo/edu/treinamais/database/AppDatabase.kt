package com.pucgo.edu.treinamais.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.pucgo.edu.treinamais.dao.AlunoDAO
import com.pucgo.edu.treinamais.dao.ProfessorDAO
import com.pucgo.edu.treinamais.dao.SessionDao
import com.pucgo.edu.treinamais.model.entity.Aluno
import com.pucgo.edu.treinamais.model.entity.ExercTreino
import com.pucgo.edu.treinamais.model.entity.Professor
import com.pucgo.edu.treinamais.model.entity.Treino

@Database(entities = [Aluno::class, Professor::class, Treino::class, ExercTreino::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun alunoDAO(): AlunoDAO
    abstract fun profDAO(): ProfessorDAO
    abstract fun sessionDAO(): SessionDao

    companion object{
        @Volatile
        private var db: AppDatabase? = null;

        fun getDB(cx: Context): AppDatabase {
            return db ?: synchronized(this) {
                val newDB = Room.databaseBuilder(
                    cx.applicationContext,
                    AppDatabase::class.java,
                    "TMDB"
                ).build()
                db = newDB
                newDB
            }
        }
    }


}