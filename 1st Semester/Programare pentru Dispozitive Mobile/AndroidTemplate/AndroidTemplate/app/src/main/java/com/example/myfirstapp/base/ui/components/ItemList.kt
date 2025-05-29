package com.example.myfirstapp.base.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstapp.base.items.Item
import com.example.myfirstapp.base.ui.theme.LocalCustomColorsPalette
import kotlin.reflect.KClass

typealias OnItemFn<T> = (T)->Unit
typealias ItemPredFun<T> = (T)->Boolean
typealias ItemSortKeyFun<T> = (T)->String

@Composable
fun <T: Item> ItemList(
    itemClass: KClass<T>,
    itemsList: List<T>,
    modifier: Modifier? = Modifier,
    onItemClick:OnItemFn<T> = {},
    sortKey:ItemSortKeyFun<T> = {""},
    filter:ItemPredFun<T> = {true},
    displayItem:(@Composable (T)->Unit)? = null
){
    Log.d("ItemList", "recompose")
    LazyColumn(
        modifier = (modifier ?: Modifier).fillMaxSize().padding(20.dp)
    ) {
        items(itemsList.sortedBy { sortKey(it) }.filter { filter(it) }) { item ->
            ItemDetail<T>(itemClass, item, onItemClick) {
                if (displayItem != null)
                    displayItem(it)
                else
                    Text(
                        text = "$it",
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                        color = if (!it.isUpdated) LocalCustomColorsPalette.current.pendingOp else Color.Unspecified
                    )
            }
        }
    }
}

@Composable
fun <T:Item> ItemDetail(itemClass:KClass<T>, item: T, onItemClick: OnItemFn<T>, displayItem:@Composable (T)->Unit) {
    Log.d("ItemDetail", "recompose id = $item")
    Row {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onItemClick(item) }
        ) {
            displayItem(item)
        }
    }
}