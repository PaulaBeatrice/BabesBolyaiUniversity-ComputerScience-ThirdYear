package com.example.myfirstapp.base

import android.util.Log

fun<T> peek(item:T?, format:(String)->String={it}):T?{
    Log.d("peek", format(item?.toString()?:"null"))
    return item
}