package com.example.myfirstapp.examples.rezervare.entity

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.myfirstapp.base.items.IItemDatabase

@Database(entities = [Rezervare::class], version = 1, exportSchema = false)
abstract class RezervareDatabase: RoomDatabase(), IItemDatabase<Rezervare, RezervareDao> {
    abstract override fun itemDao() : RezervareDao
}