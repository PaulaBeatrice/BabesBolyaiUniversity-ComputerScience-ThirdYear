package com.example.myfirstapp.Mesaje.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Mesaj")
data class Mesaj(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var text: String = "",
    var sender: String = "",
    var created: Long = 0,
    var read: Boolean = false,
    var isUpdated: Boolean = false
)