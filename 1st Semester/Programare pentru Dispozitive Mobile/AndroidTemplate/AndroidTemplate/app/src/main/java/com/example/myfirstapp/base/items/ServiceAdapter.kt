package com.example.myfirstapp.base.items

abstract class ServiceAdapter<T, S>(protected val service:S) {
    abstract suspend fun findAll() : List<T>
    abstract fun beforeUpdate(item:T) : T
    abstract suspend fun update(item:T) : T
    abstract suspend fun create(item:T):T
    abstract fun replace(list:List<T>, item:T):List<T>
    abstract fun haveSameId(item1: T, item2:T):Boolean
}