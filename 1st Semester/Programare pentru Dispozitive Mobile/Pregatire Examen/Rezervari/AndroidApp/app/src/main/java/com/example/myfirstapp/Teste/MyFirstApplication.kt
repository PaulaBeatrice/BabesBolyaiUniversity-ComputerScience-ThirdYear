package com.example.myfirstapp.Teste

import android.app.Application
import android.util.Log
import com.example.myfirstapp.Teste.core.TAG

class MyFirstApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "init")
        container = AppContainer(this)
    }
}