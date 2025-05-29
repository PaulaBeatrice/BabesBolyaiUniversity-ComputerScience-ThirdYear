package com.example.myfirstapp.orders1.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.orders1.MyFirstApplication
import com.example.myfirstapp.orders1.repository.Repository
import com.example.myfirstapp.orders1.service.Service
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.myfirstapp.orders1.core.Result
import com.example.myfirstapp.orders1.core.TAG
import com.example.myfirstapp.orders1.domeniu.OrderItem


class ItemsViewModel(private val repo: Repository, private val service : Service) : ViewModel() {
    var uiState: StateFlow<Any> = repo.itemsStream.stateIn(
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
            repo.getItems()
        }
    }

    fun update(item: OrderItem){
        Log.d(TAG, "post comdanda...")
        viewModelScope.launch {
            var list = (uiState.value as Result.Success<List<OrderItem>>).data.toList()
            val item = list.find { it.code == item.code }!!
            val savedItem = repo.update(item)
            Log.d(TAG, "AICI ${savedItem}")
            //      list.plus(savedItem)
//            (uiState.value as Result.Success<List<Rezervare>>).data.plus(savedItem)
//            rezervareRepo.update(rezervare)
//            repo.refresh();
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyFirstApplication)
                ItemsViewModel(app.container.itemRepository, app.container.service)
            }
        }
    }
}