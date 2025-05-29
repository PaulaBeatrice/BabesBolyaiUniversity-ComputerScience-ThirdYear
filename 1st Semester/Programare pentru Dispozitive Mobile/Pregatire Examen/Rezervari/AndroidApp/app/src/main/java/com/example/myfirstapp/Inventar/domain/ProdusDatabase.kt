package com.example.myfirstapp.Inventar.domain

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Produs::class], version = 1, exportSchema = false)
abstract class ProdusDatabase: RoomDatabase(){
    abstract fun produsDao(): ProdusDao
}