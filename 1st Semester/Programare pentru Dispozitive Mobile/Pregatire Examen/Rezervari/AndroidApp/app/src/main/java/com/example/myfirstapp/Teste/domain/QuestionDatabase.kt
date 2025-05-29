package com.example.myfirstapp.Teste.domain

import androidx.room.Database
import androidx.room.RoomDatabase


@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class QuestionDatabase: RoomDatabase(){
    abstract fun questionDao(): QuestionDao
}