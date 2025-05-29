package com.example.myfirstapp.base.app

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myfirstapp.base.core.Api
import com.example.myfirstapp.base.core.ItemWsClient
import com.example.myfirstapp.base.items.IItemDatabase
import com.example.myfirstapp.base.items.Item
import com.example.myfirstapp.base.items.ItemDao
import com.example.myfirstapp.base.items.Repository
import com.example.myfirstapp.base.items.ServiceAdapter
import kotlin.reflect.KClass

abstract class ItemController<T: Item, D, S: Any, SA:ServiceAdapter<T,S>>(
    itemClass: KClass<T>,
    databaseClass: KClass<D>,
    serviceClass:KClass<S>,
    serviceAdapterClass:KClass<SA>,
    context:Context)
        where D:RoomDatabase,
              D:IItemDatabase<T,ItemDao<T>> {


    class InnerRepository<T:Item,S:Any,SA:ServiceAdapter<T,S>, D:IItemDatabase<T,*>>(
        serviceAdapter: SA,
        database: D,
        itemWsClient: ItemWsClient<T>,
        context: Context
    )
    : Repository<T, S, SA, D>(
        serviceAdapter, database, itemWsClient, context
    )
    {}

    val service: S = Api.retrofit.create(serviceClass.java)
    val serviceAdapter: SA = serviceAdapterClass.constructors.first().call(service)
    val itemWsClient: ItemWsClient<T> = ItemWsClient(Api.okHttpClient, itemClass)

    val database = Room
        .databaseBuilder(context, databaseClass.java, "${itemClass.simpleName}-db")
        .allowMainThreadQueries()
        .build()

    val itemRepository: InnerRepository<T,S,SA,D> by lazy {
        InnerRepository(serviceAdapter, database, itemWsClient, context)
    }

    fun itemDao(): ItemDao<T> { return database.itemDao() }
}