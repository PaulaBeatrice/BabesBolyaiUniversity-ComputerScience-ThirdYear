package com.example.myfirstapp.Mesaje.domain

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Mesaj::class], version = 1, exportSchema = false)
abstract class MesajDatabase: RoomDatabase(){
    abstract fun mesajDao(): MesajDao
}