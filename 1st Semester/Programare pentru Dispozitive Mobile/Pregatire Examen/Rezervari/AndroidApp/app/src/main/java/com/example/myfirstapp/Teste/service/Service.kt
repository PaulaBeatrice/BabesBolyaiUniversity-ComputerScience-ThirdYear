package com.example.myfirstapp.Teste.service

import com.example.myfirstapp.Teste.domain.Question
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

data class QuestionIdsResponse(
    val token: Int,
    val questionIds: List<Int>
)



interface Service {
//    @Headers("Content-Type:application/json")
//    @POST("/auth")
//    suspend fun getQuestionIds(@Body id: Int): List<Int>
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/auth")
    suspend fun getQuestionIds(@Field("id") id: Int): QuestionIdsResponse

    @Headers("Content-Type:application/json")
    @GET("/question/{id}")
    suspend fun getQuestion(@Path("id") itemId: Int?): Question

}