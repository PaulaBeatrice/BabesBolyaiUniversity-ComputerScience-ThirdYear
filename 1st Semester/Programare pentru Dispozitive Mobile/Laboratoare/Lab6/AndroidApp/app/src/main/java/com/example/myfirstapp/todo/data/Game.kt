package com.example.myfirstapp.todo.data
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.myfirstapp.todo.ui.item.convertDateToString
import java.util.Date

@Entity
data class Game(
    @PrimaryKey val _id: String = "${System.currentTimeMillis()*10000}",
    val title: String = "",
    val price: Int = 0,
    val isSold: Boolean = false,
    val date: String = convertDateToString(Date()),
    val imageUrl: String="",
    var requiresCreate: Boolean=false,
    var requiresUpdate: Boolean=false
)