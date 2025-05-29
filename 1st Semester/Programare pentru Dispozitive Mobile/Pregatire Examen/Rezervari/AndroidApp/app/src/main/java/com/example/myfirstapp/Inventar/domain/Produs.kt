package com.example.myfirstapp.Inventar.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Produs")
data class Produs(
    @PrimaryKey(autoGenerate = false)
    var id: Int = -1,
    var nume: String = "",
    var tip: String = "",
    var cantitate: Int = 0,
    var pret: Int = 0,
    var discount: Int = 0,
    var status: Boolean = false,
    var isUpdated: Boolean = false
)