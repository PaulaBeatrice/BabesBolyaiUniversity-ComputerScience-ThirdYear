package com.example.myfirstapp.orders1.service

import com.example.myfirstapp.orders1.domeniu.OrderItem
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

data class ConfirmRequest(val code: Int, val quantity: Int)

interface Service {
    @Headers("Content-Type: application/json")
    @POST("/item")
     fun submitItem(@Body request: ConfirmRequest): OrderItem
}