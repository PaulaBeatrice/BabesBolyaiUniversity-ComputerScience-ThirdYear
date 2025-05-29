package com.example.myfirstapp.base.core

import android.util.Log
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okio.ByteString
import kotlin.reflect.KClass

class ItemWsClient<T:Any>(private val okHttpClient: OkHttpClient, val itemClass : KClass<T>) {

    lateinit var webSocket: WebSocket

    suspend fun openSocket(
        onEvent: (item: T) -> Unit,
        onClosed: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "openSocket")
            val request = Request.Builder().url(Api.wsUrl).build()
            Log.d(TAG, "newWebSocket")
            webSocket = okHttpClient.newWebSocket(
                request,
                ItemWebSocketListener(onEvent = onEvent, onClosed = onClosed, onFailure = onFailure)
            )
            Log.d(TAG, "shutdown?")
            //okHttpClient.dispatcher.executorService.shutdown()
        }
    }


    fun closeSocket() {
        Log.d(TAG, "closeSocket")
        webSocket.close(1000, "");
    }

    inner class ItemWebSocketListener(
        private val onEvent: (item: T) -> Unit,
        private val onClosed: () -> Unit,
        private val onFailure: (String) -> Unit
    ) : WebSocketListener() {
        private val moshi = Moshi.Builder().build()
        private val itemEventJsonAdapter: JsonAdapter<T> = moshi.adapter(itemClass.java)

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "onOpen")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "onMessage string $text")
            val item = itemEventJsonAdapter.fromJson(text)!!
            Log.d(TAG, "item event converted: $item")
            onEvent(item)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "onMessage bytes $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "onClosed code=$code reason=$reason")
            onClosed()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.d(TAG, "onFailure error=$t")
            onFailure(t.message?:"(no message)")
        }
    }
}