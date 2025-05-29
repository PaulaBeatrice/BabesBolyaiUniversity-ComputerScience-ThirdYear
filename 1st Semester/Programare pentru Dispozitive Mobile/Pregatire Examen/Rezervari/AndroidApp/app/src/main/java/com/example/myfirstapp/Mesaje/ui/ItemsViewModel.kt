package com.example.myfirstapp.Mesaje.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.Mesaje.MyFirstApplication
import com.example.myfirstapp.Mesaje.domain.Mesaj
import com.example.myfirstapp.Mesaje.repository.MesajRepository
import com.example.myfirstapp.Mesaje.service.Service
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.myfirstapp.Mesaje.core.Result
import com.example.myfirstapp.Mesaje.core.TAG

class ItemsViewModel(private val mesajRepository: MesajRepository, private val service : Service) : ViewModel() {
    var uiState: StateFlow<Result<List<Mesaj>>> = mesajRepository.mesajStream.stateIn(
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
            mesajRepository.refresh()
        }
    }

    fun update(mesaj:Mesaj){
        Log.d(TAG, "read message...")
        viewModelScope.launch {
            var list = (uiState.value as Result.Success<List<Mesaj>>).data.toList()
            val item = list.find { it.id == mesaj.id }!!
            val savedItem = mesajRepository.update(item)
            Log.d(TAG, "AICI ${savedItem}")
            //      list.plus(savedItem)
//            (uiState.value as Result.Success<List<Rezervare>>).data.plus(savedItem)
//            rezervareRepo.update(rezervare)
            mesajRepository.refresh();
        }
    }

//    suspend fun updateMessageFromSender(sender: String){
//        Log.d(TAG, "MARK AS READ... MESSAGES WITH SENDER ${sender}")
//        // Obține lista de mesaje folosind service.find()
//        val messages = service.find()
//
//        // Parcurge fiecare mesaj și apelează funcția update doar pentru mesajele cu senderul specificat
//        messages.filter { it.sender == sender }.forEach { message ->
//            update(message)
//        }
//
//        mesajRepository.refresh();
//    }
    suspend fun updateMessageFromSender(sender: String): List<Mesaj> {
        Log.d(TAG, "MARK AS READ... MESSAGES WITH SENDER $sender")
        val filteredMessages = service.find().filter { it.sender == sender }
        return filteredMessages
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
