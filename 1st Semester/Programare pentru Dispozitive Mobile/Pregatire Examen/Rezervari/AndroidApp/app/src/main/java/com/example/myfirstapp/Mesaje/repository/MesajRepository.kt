package com.example.myfirstapp.Mesaje.repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.myfirstapp.Mesaje.core.TAG
import com.example.myfirstapp.Mesaje.domain.Mesaj
import com.example.myfirstapp.Mesaje.domain.MesajDatabase
import com.example.myfirstapp.Mesaje.service.ItemWsClient
import com.example.myfirstapp.Mesaje.service.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import com.example.myfirstapp.Mesaje.core.Result

class MesajRepository(private val service: Service, private val database: MesajDatabase, private val itemWsClient: ItemWsClient, private val context: Context){
    private var mesaje: List<Mesaj> = listOf();


    private var itemsFlow: MutableSharedFlow<Result<List<Mesaj>>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val mesajStream: Flow<Result<List<Mesaj>>> = itemsFlow

    init {
        Log.d(TAG, "init")
    }

    suspend fun refresh() {
        Log.d(TAG, "refresh started")
        try {
            mesaje = service.find()
            database.mesajDao().clear()
            for(mesaj in mesaje) {
                mesaj.isUpdated = true
                database.mesajDao().insert(mesaj)
            }
            Log.d(TAG, "refresh succeeded")
            itemsFlow.emit(Result.Success(mesaje))
        } catch (e: Exception) {
            Log.d(TAG, "refresh failed", e)
            mesaje  = database.mesajDao().getAll()
            for(mesaj in mesaje) {
                Log.d(TAG, "ITEM ${mesaj}")
            }

            itemsFlow.emit(Result.Success(mesaje))
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

    suspend fun getItemEvents(): Flow<Result<Mesaj>> = callbackFlow {
        Log.d(TAG, "getItemEvents started")
        itemWsClient.openSocket(
            onEvent = {
                Log.d(TAG, "onEvent $it")
                if (it != null) {
                    Log.d(TAG, "onEvent trySend $it")
                    it.isUpdated = true
                    trySend(Result.Success(it))
                    Handler(Looper.getMainLooper()).post({
                        Toast.makeText(context, "S-a adaugat un mesaj nou!", Toast.LENGTH_LONG).show()
                    })
                }
            },
            onClosed = { close() },
            onFailure = { close() });
        awaitClose { itemWsClient.closeSocket() }
    }

    suspend fun update(mesaj: Mesaj): Mesaj {
        mesaj.read = true
        try {
            mesaj.isUpdated=true
            Log.d(TAG, "update $mesaj...")
            val updatedItem = service.update(mesaj, mesaj.id)
            updatedItem.isUpdated = true
            Log.d(TAG, "update $mesaj succeeded")
            handleItemUpdated(updatedItem)
            return updatedItem
        }
        catch (ex:Exception){
            Log.d(TAG, "failed update $mesaj")
            mesaj.isUpdated=false
            handleItemUpdated(mesaj)

            Handler(Looper.getMainLooper()).post({
                Toast.makeText(context, "Server unreachable. Saved locally", Toast.LENGTH_LONG).show()
            })
            return mesaj
        }
    }

    private suspend fun handleItemUpdated(mesaj: Mesaj) {
        Log.d(TAG, "handleItemUpdated...: $mesaj")
        mesaje = mesaje.map { if (it.id == mesaj.id) mesaj else it }
        database.mesajDao().update(mesaj)
        itemsFlow.emit(Result.Success(mesaje))
    }

    private suspend fun handleItemCreated(mesaj: Mesaj) {
        Log.d(TAG, "handleItemCreated...: $mesaj")
        if(!mesaje.contains(mesaj)) {
            mesaje = mesaje.plus(mesaj)
            database.mesajDao().insert(mesaj)
            mesaj.isUpdated = true
        }
        itemsFlow.emit(Result.Success(mesaje))
    }
}