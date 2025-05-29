package com.example.myfirstapp.Mesaje.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MesajDao {
    @Query("SELECT * FROM Mesaj")
    fun getAll():List<Mesaj>

    @Update
    fun update(mesaj: Mesaj)

    @Insert
    fun insert(mesaj: Mesaj)

    @Query("Delete FROM Mesaj")
    fun clear()
}