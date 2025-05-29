package com.example.myfirstapp.orders1.domeniu

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "OrderItem")
data class OrderItem(
    @PrimaryKey
    var code: Int = 0,
    var nume: String = "",
    var price: Int = 0,
    var quantity: Int =  0,
    var isUpdated: Boolean = false
)