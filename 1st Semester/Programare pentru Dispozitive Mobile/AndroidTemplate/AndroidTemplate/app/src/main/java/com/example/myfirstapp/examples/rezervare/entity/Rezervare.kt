package com.example.myfirstapp.examples.rezervare.entity

import android.content.Context
import androidx.room.*
import androidx.room.RoomDatabase
import com.example.myfirstapp.base.app.ItemController
import com.example.myfirstapp.base.items.*
import retrofit2.http.*

@Entity
data class Rezervare(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var nume: String = "",
    var doctor: String = "",
    var data: Int = 0,
    var ora: Int = 0,
    var detalii: String = "",
    var status: Boolean = false,
) : Item()

