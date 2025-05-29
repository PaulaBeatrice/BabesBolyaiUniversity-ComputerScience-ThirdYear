package com.example.myfirstapp.base.items

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myfirstapp.base.core.Result
import com.example.myfirstapp.base.ui.OnValueChanged
import com.example.myfirstapp.base.ui.components.ItemList
import com.example.myfirstapp.base.ui.components.ItemPredFun
import com.example.myfirstapp.base.ui.components.ItemSortKeyFun
import com.example.myfirstapp.base.ui.components.OnItemFn
import kotlin.reflect.KClass

open class Group(var count:Int=0):Item(isUpdated = true){}

@Composable
fun <T:Item,ID, G:Group> ItemsListWithViewModelGroupBy(
    groupClass:KClass<G>,
    itemsViewModel: ItemsViewModel<T, ID>,
    groupByKey: (T)->G,
    groupAggregate: (G,T)->G = { g,_->g },
    sortKey: ItemSortKeyFun<G> ={""},
    filter: ItemPredFun<G> ={true},
    onItemClick: OnItemFn<G> ={},
    itemsUiStateChanged: OnValueChanged<Result<*>> = {},
    modifier: Modifier
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

                val groups =(itemsUiState as Result.Success<List<T>>).data
                    .groupBy(groupByKey)
                    .map{
                        var g=it.key
                        g.count = it.value.size
                        for(item in it.value) g = groupAggregate(g, item)
                        g
                    }

                for(item in groups)
                    Log.d("ItemsListWithViewModel", item.toString())
                ItemList(
                    groupClass,
                    itemsList = groups,
                    onItemClick = onItemClick,
                    sortKey = sortKey,
                    filter = filter,
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