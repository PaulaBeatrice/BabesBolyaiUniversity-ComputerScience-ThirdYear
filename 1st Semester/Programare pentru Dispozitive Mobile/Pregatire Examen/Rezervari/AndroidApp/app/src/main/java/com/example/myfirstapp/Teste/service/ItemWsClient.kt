package com.example.myfirstapp.Teste.service

import android.util.Log

import com.example.myfirstapp.Teste.core.Api
import com.example.myfirstapp.Teste.core.TAG
import com.example.myfirstapp.Teste.domain.Question
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class ItemWsClient(private val okHttpClient: OkHttpClient) {

    lateinit var webSocket: WebSocket

    suspend fun openSocket(
        onEvent: (question: Question) -> Unit,
        onClosed: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "openSocket")
            val request = Request.Builder().url(Api.wsUrl).build()
            webSocket = okHttpClient.newWebSocket(
                request,
                ItemWebSocketListener(onEvent = onEvent, onClosed = onClosed, onFailure = onFailure)
            )
            okHttpClient.dispatcher.executorService.shutdown()
        }
    }

    fun closeSocket() {
        Log.d(TAG, "closeSocket")
        webSocket.close(1000, "");
    }

    inner class ItemWebSocketListener(
        private val onEvent: (question: Question) -> Unit,
        private val onClosed: () -> Unit,
        private val onFailure: (String) -> Unit
    ) : WebSocketListener() {
        private val moshi = Moshi.Builder().build()
        private val itemEventJsonAdapter: JsonAdapter<Question> =
            moshi.adapter(Question::class.java)

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "onOpen")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "onMessage string $text")
            val rezervare = itemEventJsonAdapter.fromJson(text)!!
            Log.d(TAG, "item event converted: $rezervare")
            onEvent(rezervare)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "onMessage bytes $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "onMessage bytes $code $reason")
            onClosed()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.d(TAG, "onMessage bytes $t")
            onFailure("failure")
        }
    }
}