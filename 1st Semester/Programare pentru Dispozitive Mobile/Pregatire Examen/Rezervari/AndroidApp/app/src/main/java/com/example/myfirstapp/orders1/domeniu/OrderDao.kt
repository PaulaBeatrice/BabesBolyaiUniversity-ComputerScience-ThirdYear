package com.example.myfirstapp.orders1.domeniu

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface OrderDao {
    @Query("SELECT * FROM OrderItem")
    fun getAll():List<OrderItem>

    @Update
    fun update(orderItem: OrderItem)

    @Insert
    fun insert(orderItem: OrderItem)

    @Query("Delete FROM OrderItem")
    fun clear()
}