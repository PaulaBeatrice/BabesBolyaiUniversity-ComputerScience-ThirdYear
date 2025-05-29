package com.example.myfirstapp.examples.rezervare.entity

import android.content.Context
import com.example.myfirstapp.base.app.ItemController

open class RezervareController(context: Context)
    : ItemController<Rezervare, RezervareDatabase, RezervareService, RezervareServiceAdapter>
    (
    Rezervare::class,
    RezervareDatabase::class, RezervareService::class, RezervareServiceAdapter::class, context){
}