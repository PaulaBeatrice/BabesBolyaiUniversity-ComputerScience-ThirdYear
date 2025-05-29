package com.example.myfirstapp.examples.ex1_messages.entity

import android.util.Log
import com.example.myfirstapp.base.core.TAG
import com.example.myfirstapp.base.items.ServiceAdapter
import com.example.myfirstapp.base.peek
import com.example.myfirstapp.examples.rezervare.entity.ConfirmRequest
import com.example.myfirstapp.examples.rezervare.entity.Rezervare
import com.example.myfirstapp.examples.rezervare.entity.RezervareService
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST


interface MessageService {
    @GET("/message")
    suspend fun findAll(): List<Message>

    /*@Headers("Content-Type:application/json")
    @POST("/confirm")
    suspend fun confirm(@Body id: ConfirmRequest): Rezervare*/
}

class MessageServiceAdapter(service: MessageService)
    : ServiceAdapter<Message, MessageService>(service) {
    override suspend fun findAll(): List<Message> = peek(service.findAll(), {"GET:/message: ${it}"})!!

    override fun haveSameId(item1: Message, item2: Message): Boolean = item1.id==item2.id

    override fun replace(list: List<Message>, item: Message): List<Message>
        = list.map { it-> if(it.id==item.id) item else it }.toList()

    override suspend fun create(item: Message): Message {
        throw NotImplementedError()
        //return service.create(item)
    }

    override suspend fun update(item: Message): Message {
        throw NotImplementedError()
        //return service.confirm(ConfirmRequest(item.id))
    }

    override fun beforeUpdate(item: Message): Message {
        return item
    }
}
