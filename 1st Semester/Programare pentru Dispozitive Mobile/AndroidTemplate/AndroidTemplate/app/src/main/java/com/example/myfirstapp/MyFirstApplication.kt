package com.example.myfirstapp

import android.app.Application
import android.util.Log
import com.example.myfirstapp.base.app.Containered
import com.example.myfirstapp.base.core.Api
import com.example.myfirstapp.base.core.TAG

import com.example.myfirstapp.examples.ex1_messages.entity.Message
import com.example.myfirstapp.examples.ex1_messages.entity.MessageController
import com.example.myfirstapp.examples.rezervare.entity.Rezervare
import com.example.myfirstapp.examples.rezervare.entity.RezervareController
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonQualifier
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.lang.reflect.Type
import java.util.Date

// Change here:

// Rezervare
// typealias AppEntity = Rezervare typealias AppContainer = RezervareController val AppUrl = "192.168.43.243:2025"

// ex1 Message
typealias AppEntity = Message typealias AppContainer = MessageController val AppUrl = "172.20.240.1:3000"


class MyFirstApplication : Application(), Containered<AppEntity, AppContainer> {
    override lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "init")
        container = AppContainer(this)
    }
}