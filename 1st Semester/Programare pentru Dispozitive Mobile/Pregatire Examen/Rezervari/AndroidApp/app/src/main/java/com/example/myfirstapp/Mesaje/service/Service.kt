package com.example.myfirstapp.Mesaje.service

import com.example.myfirstapp.Mesaje.domain.Mesaj
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface Service {
    @GET("/message")
    suspend fun find(): List<Mesaj>

    @Headers("Content-Type:application/json")
    @PUT("/message/{id}")
    suspend fun update(@Body mesaj: Mesaj, @Path("id") itemId: Int?): Mesaj
}