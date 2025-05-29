package com.example.myfirstapp.Inventar.service

import com.example.myfirstapp.Inventar.domain.Produs
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface Service {
    @GET("/products")
    suspend fun find(): List<Produs>

    @Headers("Content-Type:application/json")
    @POST("/product")
    suspend fun create(@Body produs: Produs): Produs
}