package com.example.myfirstapp.orders1

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.myfirstapp.orders1.repository.Repository

import com.example.myfirstapp.orders1.core.Api
import com.example.myfirstapp.orders1.core.TAG
import com.example.myfirstapp.orders1.domeniu.OrderItemDatabase
import com.example.myfirstapp.orders1.service.ItemWsClient
import com.example.myfirstapp.orders1.service.Service

class AppContainer(val context: Context) {
    init {
        Log.d(TAG, "init")
    }

    val service: Service = Api.retrofit.create(Service::class.java)
    val itemWsClient: ItemWsClient = ItemWsClient(Api.okHttpClient)

    val database = Room
        .databaseBuilder(context, OrderItemDatabase::class.java, "rezervari-db")
        .allowMainThreadQueries()
        .build()

    val itemRepository: Repository by lazy {
        Repository(service, database, itemWsClient, context)
    }
}