package com.example.myfirstapp.Rezervari_Android.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.Rezervari_Android.MyFirstApplication
import com.example.myfirstapp.Rezervari_Android.domain.Rezervare
import com.example.myfirstapp.Rezervari_Android.repository.RezervareRepository
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.myfirstapp.Rezervari_Android.core.TAG
import com.example.myfirstapp.Rezervari_Android.core.Result
import com.example.myfirstapp.Rezervari_Android.service.Service

class ItemsViewModel(private val rezervareRepo: RezervareRepository, private val service : Service) : ViewModel() {
    var uiState: StateFlow<Any> = rezervareRepo.rezervareStream.stateIn(
        scope = viewModelScope,
        started = WhileSubscribed(),
        initialValue = Result.Loading
    )

    init {
        Log.d(TAG, "init")
        loadItems()
    }

    fun loadItems() {
        Log.d(TAG, "loadItems...")
        viewModelScope.launch {
            rezervareRepo.refresh()
        }
    }

    fun confirm(rezervare: Rezervare){
        Log.d(TAG, "confirm rezervare...")
        viewModelScope.launch {
            var list = (uiState.value as Result.Success<List<Rezervare>>).data.toList()
            val item = list.find { it.id == rezervare.id }!!
            val savedItem = rezervareRepo.update(item)
            Log.d(TAG, "AICI ${savedItem}")
      //      list.plus(savedItem)
//            (uiState.value as Result.Success<List<Rezervare>>).data.plus(savedItem)
//            rezervareRepo.update(rezervare)
            rezervareRepo.refresh();
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
