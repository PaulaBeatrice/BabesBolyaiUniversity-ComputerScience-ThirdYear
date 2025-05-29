package com.example.myfirstapp.base.items

import android.util.Log
import com.example.myfirstapp.base.Reflection.Companion.getMutableMemberProperties
import com.example.myfirstapp.base.core.TAG
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

class ItemDefaults {
    companion object {
        fun<T:Item> createCopy(itemClass: KClass<T>, item:T):T{
            var new_item = itemClass.createInstance()
            for(prop in itemClass.getMutableMemberProperties()){
                Log.d(TAG, prop.name)
                val value = prop.getter.call(item)
                prop.setter.call(new_item, value)
            }
            Log.d(TAG,"Old item: $item")
            Log.d(TAG,"New item: $new_item")
            return new_item
        }
        private fun<T:Item, ID> getId(itemClass:KClass<T>, item:T, idPropName:String="id"):ID {
            for(prop in itemClass.getMutableMemberProperties()){
                if(prop.name==idPropName){
                    val value = prop.getter.call(item)
                    return value as ID
                }
            }
            throw Exception("Could not get id from $item")
        }

        fun <T:Item> defaultCopyItemFunc(itemClass: KClass<T>): (T)->T {
            return { createCopy(itemClass, it) }
        }

        fun<T:Item, ID> defaultGetItemId(itemClass:KClass<T>): (T)->ID{
            return { getId(itemClass, it) }
        }
    }
}