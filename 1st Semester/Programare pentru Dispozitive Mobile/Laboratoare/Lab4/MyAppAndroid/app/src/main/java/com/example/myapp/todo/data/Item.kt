package com.example.myapp.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "Products")
data class Item(@PrimaryKey val _id: String = "", val title: String = "",val price: Int=0,
                val date: String = "", val sold: Boolean=false)
