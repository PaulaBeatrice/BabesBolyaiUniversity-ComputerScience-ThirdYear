package com.example.myfirstapp.orders1

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.orders1.core.TAG
import com.example.myfirstapp.orders1.repository.Repository


class MyAppViewModel(private val repository: Repository): ViewModel(){
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