package com.example.myfirstapp.Teste.domain

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromJsonString(value: String?): List<Int>? {
        if (value == null) {
            return null
        }
        val listType = object : TypeToken<List<Int>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun toJsonString(list: List<Int>?): String? {
        if (list == null) {
            return null
        }
        return Gson().toJson(list)
    }
}
