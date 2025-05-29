package com.example.myfirstapp.Inventar.repository

import kotlin.random.Random

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.myfirstapp.Inventar.domain.Produs
import com.example.myfirstapp.Inventar.domain.ProdusDatabase
import com.example.myfirstapp.Inventar.service.ItemWsClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import com.example.myfirstapp.Inventar.service.Service
import com.example.myfirstapp.Inventar.core.TAG
import com.example.myfirstapp.Inventar.core.Result
import com.example.myfirstapp.todo.data.Game
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class ProdusRepository(private val service: Service, private val database: ProdusDatabase, private val itemWsClient: ItemWsClient, private val context: Context){
    private var produse: List<Produs> = listOf();

    private val iduriGenerate: MutableSet<Int> = mutableSetOf()


    private var itemsFlow: MutableSharedFlow<Result<List<Produs>>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val produsStream: Flow<Result<List<Produs>>> = itemsFlow

    init {
        Log.d(TAG, "init")
    }

    suspend fun refresh() {
        Log.d(TAG, "refresh started")
        try {
            produse = service.find()
            database.produsDao().clear()
            for(produs in produse) {
                produs.isUpdated = true
                database.produsDao().insert(produs)
            }
            Log.d(TAG, "refresh succeeded")
            itemsFlow.emit(Result.Success(produse))
        } catch (e: Exception) {
            Log.d(TAG, "refresh failed", e)
            produse = database.produsDao().getAll()
            for(produs in produse) {
                Log.d(TAG, "ITEM ${produs}")
            }

            itemsFlow.emit(Result.Success(produse))
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

    suspend fun getItemEvents(): Flow<Result<Produs>> = callbackFlow {
        Log.d(TAG, "getItemEvents started")
        itemWsClient.openSocket(
            onEvent = {
                Log.d(TAG, "onEvent $it")
                if (it != null) {
                    Log.d(TAG, "onEvent trySend $it")
                    trySend(Result.Success(it))
                    Handler(Looper.getMainLooper()).post({
                        Toast.makeText(context, "S-a adaugat un produs nou!", Toast.LENGTH_LONG).show()
                    })
                }
            },
            onClosed = { close() },
            onFailure = { close() });
        awaitClose { itemWsClient.closeSocket() }
    }

    suspend fun save(produs: Produs): Produs {
        try {
            Log.d(TAG, "save $produs...")
            produs.isUpdated = true
            val createdItem = service.create(produs)
            Log.d(TAG, "save $produs succeeded")
            Log.d(TAG, "handle created $createdItem")
            handleItemCreated(createdItem)
            return createdItem
        }
        catch (ex:Exception){
            var idNou = -Random.nextInt(1, Int.MAX_VALUE)
            while (iduriGenerate.contains(idNou)) {
                idNou = -Random.nextInt(1, Int.MAX_VALUE)
            }

            iduriGenerate.add(idNou)

            val createdItem = Produs(
                id = idNou,
                nume = produs.nume,
                tip = produs.tip,
                cantitate = produs.cantitate,
                pret = produs.pret,
                discount = produs.discount,
                status = produs.status,
                isUpdated = false
            )
            Log.d(TAG, "failed create on the server $produs")
            handleItemCreated(createdItem)
            Handler(Looper.getMainLooper()).post({
                Toast.makeText(context, "Server unreachable. Saved locally", Toast.LENGTH_LONG).show()
            })
            return createdItem
        }
    }

    fun quite_remove(produs: Produs){
        produse = produse.minus(produs)
        database.produsDao().deleteById(produs.id)
    }

    private suspend fun handleItemCreated(produs: Produs) {
        Log.d(TAG, "handleItemCreated...: $produs")
        if(!produse.contains(produs)) {
            produse = produse.plus(produs)
            database.produsDao().insert(produs)
            produs.isUpdated = true
        }
        itemsFlow.emit(Result.Success(produse))
    }
}