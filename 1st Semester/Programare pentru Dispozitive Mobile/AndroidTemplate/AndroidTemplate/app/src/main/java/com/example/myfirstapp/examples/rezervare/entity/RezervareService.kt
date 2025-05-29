package com.example.myfirstapp.examples.rezervare.entity

import com.example.myfirstapp.base.items.ServiceAdapter
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

class ConfirmRequest(val id: Int){}

interface RezervareService {
    @GET("/open")
    suspend fun findAll(): List<Rezervare>

    @Headers("Content-Type:application/json")
    @POST("/confirm")
    suspend fun confirm(@Body id: ConfirmRequest): Rezervare

    @Headers("Content-Type:application/json")
    @POST("/create")
    suspend fun create(@Body rezervare: Rezervare): Rezervare
}

class RezervareServiceAdapter(service: RezervareService)
    : ServiceAdapter<Rezervare, RezervareService>(service) {
    override suspend fun findAll(): List<Rezervare> = service.findAll()

    override fun haveSameId(item1: Rezervare, item2: Rezervare): Boolean = item1.id==item2.id

    override fun replace(list: List<Rezervare>, item: Rezervare): List<Rezervare>
        = list.map { it-> if(it.id==item.id) item else it }.toList()

    override suspend fun create(item: Rezervare): Rezervare = service.create(item)

    override suspend fun update(item: Rezervare): Rezervare = service.confirm(ConfirmRequest(item.id))

    override fun beforeUpdate(item: Rezervare): Rezervare {
        item.status=true
        return item
    }
}
