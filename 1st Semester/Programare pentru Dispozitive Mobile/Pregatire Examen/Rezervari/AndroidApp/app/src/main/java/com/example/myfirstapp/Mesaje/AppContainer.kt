package com.example.myfirstapp.Mesaje

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.myfirstapp.Mesaje.core.Api
import com.example.myfirstapp.Mesaje.core.TAG
import com.example.myfirstapp.Mesaje.domain.MesajDatabase
import com.example.myfirstapp.Mesaje.repository.MesajRepository
import com.example.myfirstapp.Mesaje.service.ItemWsClient
import com.example.myfirstapp.Mesaje.service.Service

class AppContainer(val context: Context) {
    init {
        Log.d(TAG, "init")
    }

    val service: Service = Api.retrofit.create(Service::class.java)
    val itemWsClient: ItemWsClient = ItemWsClient(Api.okHttpClient)

    val database = Room
        .databaseBuilder(context, MesajDatabase::class.java, "mesaj-db")
        .allowMainThreadQueries()
        .build()

    val itemRepository: MesajRepository by lazy {
        MesajRepository(service, database, itemWsClient, context)
    }
}