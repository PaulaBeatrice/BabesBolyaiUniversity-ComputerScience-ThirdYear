package com.example.myfirstapp.Teste.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "Question")
@TypeConverters(Converters::class)
data class Question(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var text: String = "",
    var options: List<Int>,
    var indexCorrectOptions: Int = 0,
    var isUpdated: Boolean = false
)