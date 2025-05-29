package com.example.myfirstapp.base.items

interface IItemDatabase<T, out Dao : ItemDao<T>> {
    fun itemDao() : Dao
}