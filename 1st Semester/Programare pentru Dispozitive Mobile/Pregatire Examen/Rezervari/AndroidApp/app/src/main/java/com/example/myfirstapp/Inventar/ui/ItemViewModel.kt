package com.example.myfirstapp.Inventar.ui

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.myfirstapp.Inventar.MyFirstApplication
import com.example.myfirstapp.Inventar.domain.Produs
import com.example.myfirstapp.Inventar.repository.ProdusRepository
import kotlinx.coroutines.launch
import com.example.myfirstapp.Inventar.core.Result
import com.example.myfirstapp.Inventar.core.TAG

data class ItemUiState(
    val itemId: Int? = null,
    val produs: Produs = Produs(),
    var loadResult: Result<Produs>? = null,
    var submitResult: Result<Produs>? = null,
)

class ItemViewModel(private val itemId: Int?, private val produsRepo: ProdusRepository) :
    ViewModel() {

    var uiState: ItemUiState by mutableStateOf(ItemUiState(loadResult = Result.Loading))
        private set

    init {
        Log.d(TAG, "init")
        if (itemId != null) {
            loadItem()
        } else {
            uiState = uiState.copy(loadResult = Result.Success(Produs()))
        }
    }

    fun loadItem() {
        viewModelScope.launch {
            produsRepo.produsStream.collect {result ->
                if (!(uiState.loadResult is Result.Loading)) {
                    return@collect
                }
                if (result is Result.Success) {
                    val items = result.data
                    val produs = items.find { it.id == itemId } ?: Produs()
                    uiState = uiState.copy(loadResult = Result.Success(produs), produs = produs)
                } else if (result is Result.Error) {
                    uiState =
                        uiState.copy(loadResult = Result.Error(result.exception))
                }
            }
        }
    }

    fun saveItem(nume : String, tip:String, cantitate:Int, pret: Int, discount:Int, status:Boolean){
        viewModelScope.launch {
            Log.d(TAG, "save new produs!!!");
            try{
                uiState = uiState.copy(submitResult = Result.Loading)
                val item = uiState.produs.copy(nume=nume, tip = tip, cantitate = cantitate, pret = pret, discount = discount, status = status)
                val savedProdus: Produs = produsRepo.save(item)
                Log.d(TAG, "save produs succeeeded!!!!");
                uiState = uiState.copy(submitResult = Result.Success(savedProdus))
            }catch (e: Exception){
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
