package com.example.myfirstapp.Rezervari_Android.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.Rezervari_Android.MyFirstApplication
import com.example.myfirstapp.Rezervari_Android.domain.Rezervare
import com.example.myfirstapp.orders1.core.Result
import com.example.myfirstapp.orders1.core.TAG
import com.example.myfirstapp.Rezervari_Android.repository.RezervareRepository
import kotlinx.coroutines.launch

data class ItemUiState(
    val itemId: Int? = null,
    val rezervare: Rezervare = Rezervare(),
    var loadResult: Result<Rezervare>? = null,
    var submitResult: Result<Rezervare>? = null,
)

class ItemViewModel(private val itemId: Int?, private val rezervareRepository: RezervareRepository) :
    ViewModel() {

    var uiState: ItemUiState by mutableStateOf(ItemUiState(loadResult = Result.Loading))
        private set

    init {
        Log.d(TAG, "init")
        if (itemId != null) {
            loadItem()
        } else {
            uiState = uiState.copy(loadResult = Result.Success(Rezervare()))
        }
    }

    fun loadItem() {
        viewModelScope.launch {
            rezervareRepository.rezervareStream.collect {result ->
                if (!(uiState.loadResult is Result.Loading)) {
                    return@collect
                }
                if (result is Result.Success) {
                    val items = result.data
                    val rezervare = items.find { it.id == itemId } ?: Rezervare()
                    uiState = uiState.copy(loadResult = Result.Success(rezervare), rezervare = rezervare)
                } else if (result is Result.Error) {
                    uiState =
                        uiState.copy(loadResult = Result.Error(result.exception))
                }
            }
        }
    }

    fun saveItem(nume : String, doctor:String, data:Int, ora: Int, detalii:String, status:Boolean){
        viewModelScope.launch {
            Log.d(TAG, "save new rezervare!!!");
            try{
                uiState = uiState.copy(submitResult = Result.Loading)
                val item = uiState.rezervare.copy(nume=nume, doctor = doctor, data = data, ora = ora, detalii = detalii, status = status)
                val savedRezervare: Rezervare = rezervareRepository.save(item)
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
                val item = uiState.rezervare.copy(nume=nume, doctor = doctor, data = data, ora = ora, detalii = detalii, status = status)
                val savedRezervare: Rezervare = rezervareRepository.update(item)
                Log.d(TAG, "UpdateItem succeeeded");
                uiState = uiState.copy(submitResult = Result.Success(savedRezervare))
            } catch (e: Exception) {
                Log.d(TAG, "saveOrUpdateItem failed");
                uiState = uiState.copy(submitResult = Result.Error(e))
            }
        }
    }

    companion object {
        fun Factory(itemId: Int?): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyFirstApplication)
                ItemViewModel(itemId, app.container.itemRepository)
            }
        }
    }
}
