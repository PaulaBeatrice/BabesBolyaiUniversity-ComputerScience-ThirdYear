package com.example.myfirstapp.Mesaje.service

import com.example.myfirstapp.Mesaje.domain.Mesaj


data class Payload(val updatedMesaj: Mesaj)
data class ItemEvent(val event: String, val payload: Payload)
