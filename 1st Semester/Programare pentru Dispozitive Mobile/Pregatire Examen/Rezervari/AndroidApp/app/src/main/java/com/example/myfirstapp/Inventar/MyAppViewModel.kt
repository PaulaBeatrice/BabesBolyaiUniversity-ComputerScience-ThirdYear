package com.example.myfirstapp.Inventar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.Inventar.core.TAG
import com.example.myfirstapp.Inventar.repository.ProdusRepository

class MyAppViewModel(private val repository: ProdusRepository): ViewModel(){
    init {
        Log.d(TAG, "init")
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyFirstApplication)
                MyAppViewModel(
                    app.container.itemRepository
                )
            }
        }
    }
}