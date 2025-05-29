package com.example.myfirstapp.Teste.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface QuestionDao {
    @Query("SELECT id FROM Question")
    fun getAll():List<Int>

    @Query("Delete FROM Question")
    fun clear()

    @Insert
    fun insert(question: Question)
}