package com.example.myfirstapp.Teste

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.example.myfirstapp.Teste.Repository.QuestionRepository
import com.example.myfirstapp.Teste.core.Api
import com.example.myfirstapp.Teste.core.TAG
import com.example.myfirstapp.Teste.domain.QuestionDatabase
import com.example.myfirstapp.Teste.service.ItemWsClient
import com.example.myfirstapp.Teste.service.Service

class AppContainer(val context: Context) {
    init {
        Log.d(TAG, "init")
    }

    val service: Service = Api.retrofit.create(Service::class.java)
    val itemWsClient: ItemWsClient = ItemWsClient(Api.okHttpClient)

    val database = Room
        .databaseBuilder(context, QuestionDatabase::class.java, "questions-db")
        .allowMainThreadQueries()
        .build()

    val itemRepository: QuestionRepository by lazy {
        QuestionRepository(service, database, itemWsClient, context)
    }
}