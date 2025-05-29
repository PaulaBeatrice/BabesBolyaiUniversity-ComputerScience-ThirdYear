package com.example.myfirstapp.Teste

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.Teste.Repository.QuestionRepository
import com.example.myfirstapp.Teste.core.TAG


class MyAppViewModel(private val repository: QuestionRepository): ViewModel(){
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