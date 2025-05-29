package com.example.myfirstapp.orders1.repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.myfirstapp.orders1.core.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import com.example.myfirstapp.orders1.core.Result
import com.example.myfirstapp.orders1.domeniu.OrderItem
import com.example.myfirstapp.orders1.domeniu.OrderItemDatabase
import com.example.myfirstapp.orders1.service.ConfirmRequest
import com.example.myfirstapp.orders1.service.ItemWsClient
import com.example.myfirstapp.orders1.service.Service


class Repository(private val service: Service, private val database: OrderItemDatabase, private val itemWsClient: ItemWsClient, private val context: Context){
    private var items: List<OrderItem> = listOf(
        OrderItem(0, "p0", 20),
        OrderItem(1, "p1", 35),
        OrderItem(2, "p2", 12),
        OrderItem(3, "p3", 45),
        OrderItem(4, "p4", 28)
    )


    private var itemsFlow: MutableSharedFlow<Result<List<OrderItem>>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val itemsStream: Flow<Result<List<OrderItem>>> = itemsFlow

    init {
        Log.d(TAG, "init")
    }

    fun getItems():List<OrderItem>{
        return items;
    }

    suspend fun refresh() {
//        Log.d(TAG, "refresh started")
//        try {
//            items = service.find()
//            database.orderDao().clear()
//            for(item in items) {
//                item.isUpdated = true
//                database.orderDao().insert(item)
//            }
//            Log.d(TAG, "refresh succeeded")
//            itemsFlow.emit(Result.Success(items))
//        } catch (e: Exception) {
//            Log.d(TAG, "refresh failed", e)
//            items  =database.orderDao().getAll()
//            for(rezervare in items) {
//                Log.d(TAG, "ITEM ${rezervare}")
//            }
//
//            itemsFlow.emit(Result.Success(items))
//            //itemsFlow.emit(Result.Error(e))
//        }
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

    suspend fun getItemEvents(): Flow<Result<OrderItem>> = callbackFlow {
        Log.d(TAG, "getItemEvents started")
        itemWsClient.openSocket(
            onEvent = {
                Log.d(TAG, "onEvent $it")
                if (it != null) {
                    Log.d(TAG, "onEvent trySend $it")
                    trySend(Result.Success(it))
                    Handler(Looper.getMainLooper()).post({
                        Toast.makeText(context, "S-a adaugat un order item nou!", Toast.LENGTH_LONG).show()
                    })
                }
            },
            onClosed = { close() },
            onFailure = { close() });
        awaitClose { itemWsClient.closeSocket() }
    }

    suspend fun update(orderItem: OrderItem): OrderItem {
        try {
            orderItem.isUpdated=true
            Log.d(TAG, "send $orderItem...")
            val updatedItem = service.submitItem(ConfirmRequest(orderItem.code, orderItem.quantity))
            updatedItem.isUpdated = true
            Log.d(TAG, "update $orderItem succeeded")
            handleItemUpdated(updatedItem)
            return updatedItem
        }
        catch (ex:Exception){
            Log.d(TAG, "failed update $orderItem")
            orderItem.isUpdated=false
            handleItemUpdated(orderItem)

            Handler(Looper.getMainLooper()).post({
                Toast.makeText(context, "Server unreachable. Saved locally", Toast.LENGTH_LONG).show()
            })
            return orderItem
        }
    }

    private suspend fun handleItemUpdated(orderItem: OrderItem) {
        Log.d(TAG, "handleItemUpdated...: $orderItem")
        items = items.map { if (it.code == orderItem.code) orderItem else it }
        database.orderDao().update(orderItem)
        itemsFlow.emit(Result.Success(items))
    }

    private suspend fun handleItemCreated(orderItem: OrderItem) {
        Log.d(TAG, "handleItemCreated...: $orderItem")
        if(!items.contains(orderItem)) {
            items = items.plus(orderItem)
            database.orderDao().insert(orderItem)
            orderItem.isUpdated = true
        }
        itemsFlow.emit(Result.Success(items))
    }


}