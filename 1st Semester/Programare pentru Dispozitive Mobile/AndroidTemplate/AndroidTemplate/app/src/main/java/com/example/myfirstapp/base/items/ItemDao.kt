package com.example.myfirstapp.base.items

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myfirstapp.base.Reflection

@Dao
abstract class ItemDao<T>(entityTableName:String? = null){
    private val tableName:String
    init{
        tableName = entityTableName ?: Reflection.getItemParameterClass(javaClass).simpleName!!
    }

    @Update
    abstract fun update(item: T)

    @Insert
    abstract fun insert(item: T)

    @Delete
    abstract fun delete(item: T)

    @Delete
    abstract fun delete(item: List<T>)

    @RawQuery
    protected abstract fun deleteAll(query: SupportSQLiteQuery): Int

    fun clear() {
        deleteAll(SimpleSQLiteQuery("DELETE FROM $tableName"))
    }

    @RawQuery
    protected abstract fun select(query: SupportSQLiteQuery): List<T>

    fun getAll():List<T>{
        return select(SimpleSQLiteQuery("SELECT * FROM $tableName"))
    }



}