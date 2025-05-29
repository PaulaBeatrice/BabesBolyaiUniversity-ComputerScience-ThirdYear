package com.example.myfirstapp.Rezervari_Android.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Rezervare")
data class Rezervare(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var nume: String = "",
    var doctor: String = "",
    var data: Int = 0,
    var ora: Int = 0,
    var detalii: String = "",
    var status: Boolean = false,
    var isUpdated: Boolean = false
)