package com.example.myfirstapp.Rezervari_Android.service

import com.example.myfirstapp.Rezervari_Android.domain.Rezervare
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

class ConfirmRequest(val id: Int){

}

interface Service {
    @GET("/open")
    suspend fun find(): List<Rezervare>

    @Headers("Content-Type:application/json")
    @POST("/confirm")
    suspend fun confirm(@Body id: ConfirmRequest): Rezervare

    @Headers("Content-Type:application/json")
    @POST("/create")
    suspend fun create(@Body rezervare: Rezervare): Rezervare

}