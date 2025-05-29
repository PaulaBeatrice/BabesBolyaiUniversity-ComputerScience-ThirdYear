package com.example.myfirstapp.base.app

import androidx.room.RoomDatabase
import com.example.myfirstapp.base.items.IItemDatabase
import com.example.myfirstapp.base.items.Item
import com.example.myfirstapp.base.items.ServiceAdapter

interface Containered<T: Item,C : ItemController<T,*,*,*>>
{
    var container:C
}