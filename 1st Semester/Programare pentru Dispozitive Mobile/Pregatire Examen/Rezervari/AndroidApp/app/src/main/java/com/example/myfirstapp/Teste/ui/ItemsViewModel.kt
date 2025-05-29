package com.example.myfirstapp.Teste.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.Teste.MyFirstApplication
import com.example.myfirstapp.Teste.Repository.QuestionRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.myfirstapp.Teste.core.Result
import com.example.myfirstapp.Teste.core.TAG
import com.example.myfirstapp.Teste.domain.Question
import kotlinx.coroutines.flow.MutableStateFlow

class ItemsViewModel(private val itemRepository: QuestionRepository) : ViewModel() {

    val uiState: StateFlow<Result<List<Int>>> = itemRepository.questionStream.stateIn(
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

//    fun getQuestionsById(id:Int){
//        Log.d(TAG, "get questions by id...")
//        viewModelScope.launch {
//            itemRepository.getQuestionsIds(id)
//        }
//    }



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