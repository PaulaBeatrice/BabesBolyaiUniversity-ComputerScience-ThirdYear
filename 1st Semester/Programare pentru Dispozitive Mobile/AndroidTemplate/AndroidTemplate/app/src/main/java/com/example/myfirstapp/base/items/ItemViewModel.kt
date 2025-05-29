package com.example.myfirstapp.base.items

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.base.app.Containered
import com.example.myfirstapp.base.app.ItemController
import com.example.myfirstapp.base.core.Result
import com.example.myfirstapp.base.core.TAG
import kotlinx.coroutines.launch
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

data class ItemUiState<T:Item, ID>(
    val itemId: ID? = null,
    val item:T,
    var loadResult: Result<T>? = null,
    var submitResult: Result<T>? = null,
)

class ItemViewModel<T:Item, ID>(
    private val itemClass: KClass<T>,
    private val itemId: ID?,
    private val repo: Repository<T, *, *, *>,
    private val getId: (T)->ID,
    private val copyItem: (T)->T
    ) :
    ViewModel() {

    var uiState: ItemUiState<T, ID> by mutableStateOf(ItemUiState(item=itemClass.createInstance(), loadResult = Result.Loading))
        private set

    init {
        Log.d(TAG, "init")
        if (itemId != null) {
            loadItem()
        } else {
            uiState = uiState.copy(loadResult = Result.Success(itemClass.createInstance()))
        }
    }

    fun loadItem() {
        viewModelScope.launch {
            repo.itemStream.collect { result ->
                if (!(uiState.loadResult is Result.Loading)) {
                    return@collect
                }
                if (result is Result.Success) {
                    val items = result.data
                    val rezervare = items.find { getId(it) == itemId } ?: itemClass.createInstance()
                    uiState = uiState.copy(loadResult = Result.Success(rezervare), item = rezervare)
                } else if (result is Result.Error) {
                    uiState =
                        uiState.copy(loadResult = Result.Error(result.exception))
                }
            }
        }
    }

    fun saveItem(newItem:T){
        viewModelScope.launch {
            Log.d(TAG, "save new rezervare!!!");
            try{
                uiState = uiState.copy(submitResult = Result.Loading)
                val item = copyItem(newItem)
                val savedRezervare: T = repo.save(item)
                Log.d(TAG, "save rezervare succeeeded!!!!");
                uiState = uiState.copy(submitResult = Result.Success(savedRezervare))
            }catch (e: Exception){
                Log.d(TAG, "saveOrUpdateItem failed");
                uiState = uiState.copy(submitResult = Result.Error(e))
            }
        }
    }

    fun UpdateItem(nume : String, doctor:String, data:Int, ora: Int, detalii:String, status:Boolean) {
        viewModelScope.launch {
            Log.d(TAG, "update rezervare!!!");
            try {
                uiState = uiState.copy(submitResult = Result.Loading)
                val item = copyItem(uiState.item)
                val savedItem: T = repo.update(item)
                Log.d(TAG, "UpdateItem succeeeded");
                uiState = uiState.copy(submitResult = Result.Success(savedItem))
            } catch (e: Exception) {
                Log.d(TAG, "saveOrUpdateItem failed");
                uiState = uiState.copy(submitResult = Result.Error(e))
            }
        }
    }

    companion object {
        fun<T:Item, ID> Factory(itemClass:KClass<T>,
                                itemId: ID?,
                                getId: (T)->ID = ItemDefaults.defaultGetItemId(itemClass),
                                copyItem: (T)->T = ItemDefaults.defaultCopyItemFunc(itemClass),
                                getContainer:()->ItemController<T,*,*,*>? = {null})
        : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val controller =
                    getContainer()?:
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                            as Containered<T,ItemController<T,*,*,*>>).container
                ItemViewModel<T, ID>(itemClass, itemId, controller.itemRepository, getId, copyItem)
            }
        }
    }
}