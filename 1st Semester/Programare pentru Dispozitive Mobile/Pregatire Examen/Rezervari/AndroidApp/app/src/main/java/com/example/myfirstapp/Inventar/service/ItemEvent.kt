package com.example.myfirstapp.Inventar.service

import com.example.myfirstapp.Inventar.domain.Produs


data class Payload(val updatedProdus: Produs)
data class ItemEvent(val event: String, val payload: Payload)
