package com.example.myfirstapp.Teste.Repository

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.example.myfirstapp.Teste.core.TAG
import com.example.myfirstapp.Teste.domain.Question
import com.example.myfirstapp.Teste.domain.QuestionDatabase
import com.example.myfirstapp.Teste.service.ItemWsClient
import com.example.myfirstapp.Teste.service.Service
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import com.example.myfirstapp.Teste.core.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class QuestionRepository(private val service: Service, private val database: QuestionDatabase, private val itemWsClient: ItemWsClient, private val context: Context){
    private var questions: List<Int> = listOf();


    private var itemsFlow: MutableSharedFlow<Result<List<Int>>> = MutableSharedFlow(
        replay = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val questionStream: Flow<Result<List<Int>>> = itemsFlow

    init {
        Log.d(TAG, "init")
    }

    suspend fun refresh() {
//        Log.d(TAG, "refresh started")
//        try {
//            questions = service.getQuestionIds()
//            database.questionDao().clear()
//            for(question in questions) {
////                question.isUpdated = true
//                val q = Question(
//                    id = question,
//                    text = "",
//                    options = ArrayList<Int>(),
//                    indexCorrectOptions = 0,
//                    isUpdated = true
//                )
//                database.questionDao().insert(q)
//            }
//            Log.d(TAG, "refresh succeeded")
//            itemsFlow.emit(Result.Success(questions))
//        } catch (e: Exception) {
//            Log.d(TAG, "refresh failed", e)
//            questions  =database.questionDao().getAll()
//            for(question in questions) {
//                Log.d(TAG, "ITEM ${question}")
//            }
//
//            itemsFlow.emit(Result.Success(questions))
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

    suspend fun getItemEvents(): Flow<Result<Question>> = callbackFlow {
        Log.d(TAG, "getItemEvents started")
        val onEvent: (Question) -> Unit = { question ->
            Log.d(TAG, "onEvent $question")
            Log.d(TAG, "onEvent trySend $question")
            question.isUpdated = true
            trySend(Result.Success(question))
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, "S-a adaugat o intrebare noua!", Toast.LENGTH_LONG).show()
            }
        }
        val onClosed: () -> Unit = {
            Log.d(TAG, "events closed1!!")
            close()
        }
        var onFailureBak: (String) -> Unit = {}
        val onFailure: (String) -> Unit = { error ->
            Log.d(TAG, "events failed!!")
            runBlocking {
                withContext(Dispatchers.IO) {
                    delay(2000)
                }
                itemWsClient.openSocket(onEvent = onEvent, onClosed = onClosed, onFailure = onFailureBak)
            }
        }
        onFailureBak = onFailure
        itemWsClient.openSocket(onEvent = onEvent, onClosed = onClosed, onFailure = onFailure)
        Log.d(TAG, "After openSocket")
        awaitClose {
            Log.d(TAG, "events closed2!!")
            itemWsClient.closeSocket()
        }
//        Log.d(TAG, "getItemEvents started")
//        itemWsClient.openSocket(
//            onEvent = {
//                Log.d(TAG, "onEvent $it")
//                if (it != null) {
//                    Log.d(TAG, "onEvent trySend $it")
//                    it.isUpdated = true
//                    trySend(Result.Success(it))
//                    Handler(Looper.getMainLooper()).post({
//                        Toast.makeText(context, "S-a adaugat o intrebare noua!", Toast.LENGTH_LONG).show()
//                    })
//                }
//            },
//            onClosed = { Log.d(TAG, "events closed1!!"); close()},
//            onFailure = { close() });
//        awaitClose { itemWsClient.closeSocket() }
    }

    private suspend fun handleItemCreated(question: Question) {
        Log.d(TAG, "handleItemCreated...: $question")
        if(!questions.contains(question.id)) {
            question.isUpdated = true
            questions = questions.plus(question.id)
            database.questionDao().insert(question)
//            question.isUpdated = true
        }
        itemsFlow.emit(Result.Success(questions))
    }

//    suspend fun getQuestionsIds(id: Int):List<Int>{
//        Log.d(TAG, "get ids ")
//        try {
//            Log.d(TAG, "get ids for $id...")
//            var questionsIds = database.questionDao().getAll()
//            return questionsIds
//        }
//        catch (ex:Exception){
//            Log.d(TAG, "failed get Ids")
//            Handler(Looper.getMainLooper()).post({
//                Toast.makeText(context, "Server unreachable. Saved locally", Toast.LENGTH_LONG).show()
//            })
//            throw ex
//        }
//    }
}