package com.example.myfirstapp.base.items

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.myfirstapp.base.core.ItemWsClient
import com.example.myfirstapp.base.core.Result
import com.example.myfirstapp.base.core.TAG
import com.example.myfirstapp.base.ui.Sys.Companion.makeToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

open class Repository<T:Item, Serv, ServAdpt: ServiceAdapter<T, Serv>, Database : IItemDatabase<T, ItemDao<T>>>(
    protected val serviceAdapter: ServAdpt,
    protected val database: Database,
    protected val itemWsClient: ItemWsClient<T>,
    protected val context: Context) {

    private var items: List<T> = listOf();

    private var itemsFlow: MutableSharedFlow<Result<List<T>>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val itemStream: Flow<Result<List<T>>> = itemsFlow

    init {
        Log.d(TAG, "init")
    }

    suspend fun refresh() {
        Log.d(TAG, "refresh started")
        try {
            items = serviceAdapter.findAll()
            database.itemDao().clear()
            for(item in items) {
                item.isUpdated = true
                database.itemDao().insert(item)
            }
            Log.d(TAG, "refresh succeeded")
            itemsFlow.emit(Result.Success(items))
        } catch (e: Exception) {
            Log.d(TAG, "refresh failed", e)
            items  = database.itemDao().getAll()
            for(item in items) {
                Log.d(TAG, "ITEM ${item}")
            }

            itemsFlow.emit(Result.Success(items))
            //itemsFlow.emit(Result.Error(e))
        }
    }

    suspend fun openWsClient() {
        Log.d(TAG, "openWsClient")
        withContext(Dispatchers.IO) {
            getItemEvents().collect {
                Log.d(TAG, "Item event collected $it")
                if (it is Result.Success) {
                    if(items.any{item->serviceAdapter.haveSameId(it.data, item)})
                        handleItemUpdated(it.data)
                    else
                        handleItemCreated(it.data)
                }
            }
            Log.d(TAG, "end of openWsClient")
        }
    }

    suspend fun closeWsClient() {
        Log.d(TAG, "closeWsClient")
        withContext(Dispatchers.IO) {
            itemWsClient.closeSocket()
        }
    }

    suspend fun getItemEvents(): Flow<Result<T>> = callbackFlow {
        Log.d(TAG, "getItemEvents started")
        val onEvent:(T)->Unit = {
            Log.d(TAG, "onEvent $it")
            Log.d(TAG, "onEvent trySend $it")
            trySend(Result.Success(it))
            context.makeToast("S-a adaugat un item nou")
        }
        val onClosed: ()->Unit = { Log.d(TAG, "events closed1!!"); close() }
        var onFailureBak: (String)->Unit = {}
        val onFailure: (String)->Unit = { it ->
            Log.d(TAG, "events failed!!");
            context.makeToast("ws failure: $it")
            runBlocking {
                withContext(Dispatchers.IO) { Thread.sleep(2000) }
                itemWsClient.openSocket(onEvent = onEvent, onClosed = onClosed, onFailure = onFailureBak);
            }
        }
        onFailureBak = onFailure

        itemWsClient.openSocket(onEvent = onEvent, onClosed = onClosed, onFailure = onFailure);
        Log.d(TAG, "After openSocket")
        awaitClose { Log.d(TAG, "events closed2!!"); itemWsClient.closeSocket() }
    }

    suspend fun update(itemToUpdate: T): T {
        val item = serviceAdapter.beforeUpdate(itemToUpdate)
        try {
            item.isUpdated=true
            Log.d(TAG, "update $item...")
            val updatedItem = serviceAdapter.update(item)
            updatedItem.isUpdated = true
            Log.d(TAG, "update $item succeeded")
            handleItemUpdated(updatedItem)
            return updatedItem
        }
        catch (ex:Exception){
            Log.d(TAG, "failed update $item")
            item.isUpdated=false
            handleItemUpdated(item)

            Handler(Looper.getMainLooper()).post({
                Toast.makeText(context, "Server unreachable. Saved locally", Toast.LENGTH_LONG).show()
            })
            return item
        }
    }

    suspend fun save(item: T): T {
        try {
            Log.d(TAG, "save $item...")
            val createdItem = serviceAdapter.create(item)
            Log.d(TAG, "save $item succeeded")
            Log.d(TAG, "handle created $createdItem")
            handleItemCreated(createdItem)
            return createdItem
        }
        catch (ex:Exception){
            Log.d(TAG, "failed create on the server $item")
            Handler(Looper.getMainLooper()).post({
                Toast.makeText(context, "Server unreachable. Saved locally", Toast.LENGTH_LONG).show()
            })
            throw ex
        }
    }

    private suspend fun handleItemUpdated(item: T) {
        Log.d(TAG, "handleItemUpdated...: $item")
        items = serviceAdapter.replace(items, item)
        database.itemDao().update(item)
        itemsFlow.emit(Result.Success(items))
    }

    private suspend fun handleItemCreated(item: T) {
        Log.d(TAG, "handleItemCreated...: $item")
        if(!items.contains(item)) {
            items = items.plus(item)
            database.itemDao().insert(item)
            item.isUpdated = true
        }
        itemsFlow.emit(Result.Success(items))
    }
}