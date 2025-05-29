package com.example.myfirstapp.examples.ex1_messages.entity

import android.content.Context
import com.example.myfirstapp.base.app.ItemController

open class MessageController(context: Context)
    : ItemController<Message, MessageDatabase, MessageService, MessageServiceAdapter>(
        Message::class,
        MessageDatabase::class,
        MessageService::class,
        MessageServiceAdapter::class,
    context
    )