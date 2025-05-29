package com.example.myfirstapp.base.ui.components

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfirstapp.base.items.Item
import com.example.myfirstapp.base.items.ItemsViewModel
import com.example.myfirstapp.base.ui.OnValueChanged
import com.example.myfirstapp.base.core.Result
import com.example.myfirstapp.examples.rezervare.entity.Rezervare
import kotlin.reflect.KClass

@Composable
fun <T: Item,ID> ItemsListWithViewModel(
    itemClass: KClass<T>,
    itemsViewModel: ItemsViewModel<T, ID>,
    sortKey: ItemSortKeyFun<T> ={""},
    filter: ItemPredFun<T> ={true},
    onItemClick: OnItemFn<T> ={},
    itemsUiStateChanged: OnValueChanged<Result<*>> = {},
    modifier: Modifier,
    displayItem: (@Composable (T)->Unit)? = null
) {
    val itemsUiState by itemsViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(itemsUiState){
        itemsUiStateChanged(itemsUiState)
    }

    LaunchedEffect(true){
        itemsViewModel.loadItems()
    }

    Scaffold(modifier=modifier) {
        when (itemsUiState) {
            is Result.Success -> {
                Log.d("ItemsListWithViewModel", "SUCCESS")
                Log.d("ItemsListWithViewModel", "Count = ${(itemsUiState as Result.Success<List<T>>).data.size}")
                for(item in (itemsUiState as Result.Success<List<T>>).data)
                    Log.d("ItemsListWithViewModel", item.toString())
                ItemList(
                    itemClass,
                    itemsList = (itemsUiState as Result.Success<List<T>>).data,
                    onItemClick = onItemClick,
                    sortKey = sortKey,
                    filter = filter,
                    displayItem = displayItem,
                    modifier = Modifier.padding(it)
                )
            }
            is Result.Loading -> {
                Log.d("ItemsListWithViewModel", "Loading")
                CircularProgressIndicator(modifier = Modifier.padding(it))
            }
            is Result.Error -> {
                Log.d("ItemsListWithViewModel", "Error!!")
                Text(
                    text = "Failed to load items - ${(itemsUiState as Result.Error).exception?.message}",
                    modifier = Modifier.padding(it)
                )
            }
        }
    }

}