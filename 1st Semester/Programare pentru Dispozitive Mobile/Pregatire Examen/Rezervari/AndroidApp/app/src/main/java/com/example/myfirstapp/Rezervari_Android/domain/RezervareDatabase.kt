package com.example.myfirstapp.Rezervari_Android.domain

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Rezervare::class], version = 1, exportSchema = false)
abstract class RezervareDatabase: RoomDatabase(){
    abstract fun rezervareDao():RezervareDao
}