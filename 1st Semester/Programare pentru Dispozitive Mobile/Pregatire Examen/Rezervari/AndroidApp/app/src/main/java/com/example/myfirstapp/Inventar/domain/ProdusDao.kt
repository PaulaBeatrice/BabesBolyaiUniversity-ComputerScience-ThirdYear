package com.example.myfirstapp.Inventar.domain

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ProdusDao {
    @Query("SELECT * FROM Produs")
    fun getAll():List<Produs>

    @Insert
    fun insert(produs: Produs)

    @Query("Delete FROM Produs")
    fun clear()

    @Query("DELETE FROM Produs WHERE id == :id")
    fun deleteById(id:Int)
}