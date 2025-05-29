package com.example.myfirstapp.Rezervari_Android.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RezervareDao {
    @Query("SELECT * FROM Rezervare")
    fun getAll():List<Rezervare>

    @Update
    fun update(rezervare: Rezervare)

    @Insert
    fun insert(rezervare: Rezervare)

    @Query("Delete FROM Rezervare")
    fun clear()
}