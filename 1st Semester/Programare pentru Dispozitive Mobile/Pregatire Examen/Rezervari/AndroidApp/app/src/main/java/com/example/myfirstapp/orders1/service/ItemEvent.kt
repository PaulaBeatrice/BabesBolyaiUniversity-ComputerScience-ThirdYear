package com.example.myfirstapp.orders1.service

import com.example.myfirstapp.orders1.domeniu.OrderItem

data class Payload(val updatedOrderItem: OrderItem)
data class ItemEvent(val event: String, val payload: Payload)
