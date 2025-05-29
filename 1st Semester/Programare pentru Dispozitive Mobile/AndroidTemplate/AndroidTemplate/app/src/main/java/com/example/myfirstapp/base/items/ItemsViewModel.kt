package com.example.myfirstapp.base.items

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.base.app.Containered
import com.example.myfirstapp.base.app.ItemController
import com.example.myfirstapp.base.core.Result
import com.example.myfirstapp.base.core.TAG
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.reflect.KClass

class ItemsViewModel<T:Item, ID>(
    private val itemClass: KClass<T>,
    private val repo: Repository<T, *, *, *>,
    private val getId:(T)->ID = ItemDefaults.defaultGetItemId(itemClass))
    : ViewModel() {
    var uiState: StateFlow<Result<List<T>>> = repo.itemStream.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Result.Loading
    )

    init {
        Log.d(TAG, "init")
        loadItems()
    }

    fun loadItems() {
        Log.d(TAG, "loadItems...")
        viewModelScope.launch {
            repo.refresh()
        }
    }

    fun update(item: T) {
        Log.d(TAG, "update item...")
        viewModelScope.launch {
            val list = (uiState.value as Result.Success<List<T>>).data.toList()
            val updatedItem = list.find { getId(it) == getId(item) }!!
            repo.update(updatedItem)
            repo.refresh()
        }
    }

    companion object {
        fun <T:Item, ID> Factory(
            itemClass:KClass<T>,
            getId: (T)->ID = ItemDefaults.defaultGetItemId(itemClass),
            getContainer:()->ItemController<T,*,*,*>? = {null})
        : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val controller =
                    getContainer()?:
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as Containered<T, ItemController<T, *, *, *>>).container
                ItemsViewModel(itemClass, controller.itemRepository, getId)
            }
        }
    }
}

@Composable
inline fun <reified T:Item, ID> createItemsViewModel():ItemsViewModel<T,ID>{
    return viewModel<ItemsViewModel<T, ID>>(factory = ItemsViewModel.Factory<T,ID>(T::class))
}