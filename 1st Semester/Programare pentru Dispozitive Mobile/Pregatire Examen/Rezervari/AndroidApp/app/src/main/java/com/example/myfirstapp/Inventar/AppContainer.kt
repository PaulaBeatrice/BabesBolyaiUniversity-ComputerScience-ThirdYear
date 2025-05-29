package com.example.myfirstapp.Inventar

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.myfirstapp.Inventar.core.Api
import com.example.myfirstapp.Inventar.core.TAG
import com.example.myfirstapp.Inventar.domain.ProdusDatabase
import com.example.myfirstapp.Inventar.repository.ProdusRepository
import com.example.myfirstapp.Inventar.service.ItemWsClient
import com.example.myfirstapp.Inventar.service.Service

class AppContainer(val context: Context) {
    init {
        Log.d(TAG, "init")
    }

    val service: Service = Api.retrofit.create(Service::class.java)
    val itemWsClient: ItemWsClient = ItemWsClient(Api.okHttpClient)

    val database = Room
        .databaseBuilder(context, ProdusDatabase::class.java, "produs-db")
        .allowMainThreadQueries()
        .build()

    val itemRepository: ProdusRepository by lazy {
        ProdusRepository(service, database, itemWsClient, context)
    }
}