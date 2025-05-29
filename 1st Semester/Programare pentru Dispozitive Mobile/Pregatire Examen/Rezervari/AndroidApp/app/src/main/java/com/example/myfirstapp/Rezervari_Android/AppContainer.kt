package com.example.myfirstapp.Rezervari_Android

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.myfirstapp.Rezervari_Android.core.Api
import com.example.myfirstapp.Rezervari_Android.core.TAG
import com.example.myfirstapp.Rezervari_Android.domain.RezervareDatabase
import com.example.myfirstapp.Rezervari_Android.repository.RezervareRepository
import com.example.myfirstapp.Rezervari_Android.service.ItemWsClient
import com.example.myfirstapp.Rezervari_Android.service.Service

class AppContainer(val context: Context) {
    init {
        Log.d(TAG, "init")
    }

    val service: Service = Api.retrofit.create(Service::class.java)
    val itemWsClient: ItemWsClient = ItemWsClient(Api.okHttpClient)

    val database = Room
        .databaseBuilder(context, RezervareDatabase::class.java, "rezervari-db")
        .allowMainThreadQueries()
        .build()

    val itemRepository: RezervareRepository by lazy {
        RezervareRepository(service, database, itemWsClient, context)
    }
}