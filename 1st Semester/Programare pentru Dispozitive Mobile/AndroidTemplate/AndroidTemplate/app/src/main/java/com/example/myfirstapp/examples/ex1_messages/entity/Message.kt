package com.example.myfirstapp.examples.ex1_messages.entity

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.myfirstapp.base.app.DateConverters
import com.example.myfirstapp.base.items.IItemDatabase
import com.example.myfirstapp.base.items.Item
import com.example.myfirstapp.base.items.ItemDao
import com.squareup.moshi.JsonClass
import java.util.Date

@Entity
data class Message(
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0,
    var text: String = "",
    var read: Boolean = false,
    var sender: String = "",
    var created: Long = Date().time
) : Item()

@Dao
abstract class MessageDao : ItemDao<Message>() { }

@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class MessageDatabase: RoomDatabase(), IItemDatabase<Message, MessageDao> {
    abstract override fun itemDao() : MessageDao
}
