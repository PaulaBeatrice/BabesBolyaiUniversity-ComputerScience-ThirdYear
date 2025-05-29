package com.example.myfirstapp.Rezervari_Android.service

import com.example.myfirstapp.Rezervari_Android.domain.Rezervare

data class Payload(val updatedRezervare: Rezervare)
data class ItemEvent(val event: String, val payload: Payload)
