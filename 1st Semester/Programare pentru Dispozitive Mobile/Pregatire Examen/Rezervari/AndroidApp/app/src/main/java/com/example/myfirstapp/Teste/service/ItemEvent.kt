package com.example.myfirstapp.Teste.service

import com.example.myfirstapp.Teste.domain.Question


data class Payload(val updatedQuestion: Question)
data class ItemEvent(val event: String, val payload: Payload)
