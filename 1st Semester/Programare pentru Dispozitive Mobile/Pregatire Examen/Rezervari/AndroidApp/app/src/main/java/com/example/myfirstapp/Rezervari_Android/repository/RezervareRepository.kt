package com.example.myfirstapp.Rezervari_Android.repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.myfirstapp.orders1.core.TAG
import com.example.myfirstapp.Rezervari_Android.domain.Rezervare
import com.example.myfirstapp.Rezervari_Android.domain.RezervareDatabase
import com.example.myfirstapp.Rezervari_Android.service.ItemWsClient
import com.example.myfirstapp.Rezervari_Android.service.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import com.example.myfirstapp.orders1.core.Result
import com.example.myfirstapp.Rezervari_Android.service.ConfirmRequest
import com.example.myfirstapp.Rezervari_Android.core.Api


class RezervareRepository(private val service: Service, private val database: RezervareDatabase, private val itemWsClient: ItemWsClient, private val context: Context){
    private var rezervari: List<Rezervare> = listOf();


    private var itemsFlow: MutableSharedFlow<Result<List<Rezervare>>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val rezervareStream: Flow<Result<List<Rezervare>>> = itemsFlow

    init {
        Log.d(TAG, "init")
    }

    suspend fun refresh() {
        Log.d(TAG, "refresh started")
        try {
            rezervari = service.find()
            database.rezervareDao().clear()
            for(rezervare in rezervari) {
                rezervare.isUpdated = true
                database.rezervareDao().insert(rezervare)
            }
            Log.d(TAG, "refresh succeeded")
            itemsFlow.emit(Result.Success(rezervari))
        } catch (e: Exception) {
            Log.d(TAG, "refresh failed", e)
            rezervari  =database.rezervareDao().getAll()
            for(rezervare in rezervari) {
                Log.d(TAG, "ITEM ${rezervare}")
            }

            itemsFlow.emit(Result.Success(rezervari))
            //itemsFlow.emit(Result.Error(e))
        }
    }

    suspend fun openWsClient() {
        Log.d(TAG, "openWsClient")
        withContext(Dispatchers.IO) {
            getItemEvents().collect {
                Log.d(TAG, "Item event collected $it")
                if (it is Result.Success) {
                   handleItemCreated(it.data)
                }
            }
        }
    }

    suspend fun closeWsClient() {
        Log.d(TAG, "closeWsClient")
        withContext(Dispatchers.IO) {
            itemWsClient.closeSocket()
        }
    }

    suspend fun getItemEvents(): Flow<Result<Rezervare>> = callbackFlow {
        Log.d(TAG, "getItemEvents started")
        itemWsClient.openSocket(
            onEvent = {
                Log.d(TAG, "onEvent $it")
                if (it != null) {
                    Log.d(TAG, "onEvent trySend $it")
                    trySend(Result.Success(it))
                    Handler(Looper.getMainLooper()).post({
                        Toast.makeText(context, "S-a adaugat o rezervare noua!", Toast.LENGTH_LONG).show()
                    })
                }
            },
            onClosed = { close() },
            onFailure = { close() });
        awaitClose { itemWsClient.closeSocket() }
    }

    suspend fun update(rezervare: Rezervare): Rezervare {
        rezervare.status = true
        try {
            rezervare.isUpdated=true
            Log.d(TAG, "update $rezervare...")
            val updatedItem = service.confirm(ConfirmRequest(rezervare.id))
            updatedItem.isUpdated = true
            Log.d(TAG, "update $rezervare succeeded")
            handleItemUpdated(updatedItem)
            return updatedItem
        }
        catch (ex:Exception){
            Log.d(TAG, "failed update $rezervare")
            rezervare.isUpdated=false
            handleItemUpdated(rezervare)

            Handler(Looper.getMainLooper()).post({
                Toast.makeText(context, "Server unreachable. Saved locally", Toast.LENGTH_LONG).show()
            })
            return rezervare
        }
    }

    suspend fun save(rezervare: Rezervare): Rezervare {
        try {
            Log.d(TAG, "save $rezervare...")
            val createdItem = service.create(rezervare)
            Log.d(TAG, "save $rezervare succeeded")
            Log.d(TAG, "handle created $createdItem")
            handleItemCreated(createdItem)
            return createdItem
        }
        catch (ex:Exception){
            Log.d(TAG, "failed create on the server $rezervare")
            Handler(Looper.getMainLooper()).post({
                Toast.makeText(context, "Server unreachable. Saved locally", Toast.LENGTH_LONG).show()
            })
            throw ex
        }
    }

    private suspend fun handleItemUpdated(rezervare: Rezervare) {
        Log.d(TAG, "handleItemUpdated...: $rezervare")
        rezervari = rezervari.map { if (it.id == rezervare.id) rezervare else it }
        database.rezervareDao().update(rezervare)
        itemsFlow.emit(Result.Success(rezervari))
    }

    private suspend fun handleItemCreated(rezervare: Rezervare) {
        Log.d(TAG, "handleItemCreated...: $rezervare")
        if(!rezervari.contains(rezervare)) {
            rezervari = rezervari.plus(rezervare)
            database.rezervareDao().insert(rezervare)
            rezervare.isUpdated = true
        }
        itemsFlow.emit(Result.Success(rezervari))
    }

    private fun getBearerToken() = "Bearer ${Api.tokenInterceptor.token}"


}