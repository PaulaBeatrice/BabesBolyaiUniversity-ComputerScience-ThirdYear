package com.example.myfirstapp.base

import android.util.Log

class SortKeyUtil {
    companion object{
        fun <T:Any> peek(it:T):T{
            Log.d("PEEK", it.toString())
            return it
        }

        fun <T:Any> asc(it:T, pad:Int=20): String = it.toString().padEnd(pad,'\u0000')
        fun <T:Any> desc(it:T, pad:Int=20): String = asc(it,pad).map{ c -> Char(0xffff-c.code) }.joinToString("")

        fun asc(it:Int): String = asc(it, pad=11)
        fun desc(it:Int): String = desc(it, pad=11)
    }
}