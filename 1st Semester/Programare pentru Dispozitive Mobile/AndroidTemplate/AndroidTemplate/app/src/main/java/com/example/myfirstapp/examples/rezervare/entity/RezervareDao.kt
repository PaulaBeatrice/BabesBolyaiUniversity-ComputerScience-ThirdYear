package com.example.myfirstapp.examples.rezervare.entity

import androidx.room.Dao
import com.example.myfirstapp.base.items.ItemDao

@Dao
abstract class RezervareDao : ItemDao<Rezervare>() { }