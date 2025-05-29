package com.example.myfirstapp.Inventar.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.Inventar.MyFirstApplication
import com.example.myfirstapp.Inventar.domain.Produs
import com.example.myfirstapp.Inventar.repository.ProdusRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.myfirstapp.Inventar.core.Result
import com.example.myfirstapp.Inventar.core.TAG

class ItemsViewModel(private val itemRepository: ProdusRepository) : ViewModel() {
    val uiState: StateFlow<Result<List<Produs>>> = itemRepository.produsStream.stateIn(
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
            itemRepository.refresh()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyFirstApplication)
                ItemsViewModel(app.container.itemRepository)
            }
        }
    }
}