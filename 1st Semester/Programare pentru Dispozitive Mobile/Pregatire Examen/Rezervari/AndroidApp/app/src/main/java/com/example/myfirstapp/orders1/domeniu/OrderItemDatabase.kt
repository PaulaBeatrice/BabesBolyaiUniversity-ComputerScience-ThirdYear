package com.example.myfirstapp.orders1.domeniu

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OrderItem::class], version = 1, exportSchema = false)
abstract class OrderItemDatabase: RoomDatabase(){
    abstract fun orderDao(): OrderDao
}