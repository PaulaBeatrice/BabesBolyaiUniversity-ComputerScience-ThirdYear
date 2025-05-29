package com.example.myfirstapp.base

import android.util.Log
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties

class Reflection{
    companion object{
        fun getItemParameterClass(javaClass:Class<*>): KClass<*> {
            var class_it:Class<*> = javaClass
            while(!(class_it.genericSuperclass is ParameterizedType)) {
                Log.d("getItemParameterClass", class_it.canonicalName!!)
                class_it = class_it.superclass
            }
            Log.d("getItemParameterClass", "FOUND ${class_it.name}")
            val genericSuperclass = class_it.genericSuperclass!!
            val type: Type = (genericSuperclass as ParameterizedType).actualTypeArguments[0]
            return (if (type is Class<*>) (type as Class<*>) else ((type as ParameterizedType).rawType as Class<*>)).kotlin
        }

        fun<T:Any> KClass<T>.getMutableMemberProperties(): List<KMutableProperty<*>> {
            return this.memberProperties.filterIsInstance<KMutableProperty<*>>()
        }
    }
}