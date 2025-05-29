package com.example.myfirstapp.Mesaje.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myfirstapp.Mesaje.MyFirstApplication
import com.example.myfirstapp.Mesaje.core.ConnectionState
import com.example.myfirstapp.Mesaje.core.PendingWorker
import com.example.myfirstapp.Mesaje.core.TAG
import com.example.myfirstapp.Mesaje.core.connectivityState
import com.example.myfirstapp.R
import com.example.myfirstapp.Mesaje.core.Result
import com.example.myfirstapp.Mesaje.domain.Mesaj


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onItemClick: (id: Int?) -> Unit, onClose: () -> Unit){
    Log.d("ItemsScreen", "Recompose")
    val itemsViewModel = viewModel<ItemsViewModel>(factory = ItemsViewModel.Factory)
    val itemsUiState by itemsViewModel.uiState.collectAsStateWithLifecycle()


    val app = LocalContext.current.applicationContext as MyFirstApplication;
    val service = app.container.service;
    val workManager = WorkManager.getInstance(app)
    var isOffline by rememberSaveable { mutableStateOf(true) }
    val networkConnectivity by connectivityState()
    var refreshButtonVisibility by rememberSaveable {
        mutableStateOf(0.0f)
    }
    var backButtonVisibility by rememberSaveable {
        mutableStateOf(0.0f)
    }

    var selectedSender by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedSender) {
        if (selectedSender != null) {
            // cand alegem sender, inlocuim `ItemListMapped` cu `ItemListFilteredBySender` si afisam butonul back
            backButtonVisibility = 1.0f
            for( mesaj in (itemsUiState as Result.Success<List<Mesaj>>).data.filter { it.sender == selectedSender})
                itemsViewModel.update(mesaj)
//            for(mesaj in itemsViewModel.updateMessageFromSender(selectedSender!!))
//            {
//                itemsViewModel.update(mesaj)
//            }
        } else {
            // nu avem sender selectat => afisam `ItemListMapped`, si nu afisam butonul de back
            backButtonVisibility = 0.0f
        }
    }


    LaunchedEffect(networkConnectivity) {
        if(isOffline) {
            if (networkConnectivity == ConnectionState.Available) {
                val request = OneTimeWorkRequestBuilder<PendingWorker>().build()
                workManager.enqueue(request)
            }
        }
        isOffline = networkConnectivity == ConnectionState.Unavailable
    }

    var loaded by rememberSaveable {
        mutableStateOf(false)
    }

    LaunchedEffect( true){
        Log.d(TAG,"loaded = true")
        loaded = true
    }


    LaunchedEffect(itemsUiState){
        val cnt = if(itemsUiState is Result.Loading) 0 else
            (itemsUiState as Result.Success<List<Mesaj>>).data.size
        if(cnt == 0)
            refreshButtonVisibility = 1.0f
        else
            refreshButtonVisibility = 0.0f
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.main_page)) },
                actions = {
                    Button(onClick = {
                        if (selectedSender != null) {
                            // daca e selectat senderul revenim la lista initiala
                            selectedSender = null
                        } else {
                            // revenim la lista initiala
                            itemsViewModel.loadItems()
                        }
                    },
                        modifier = Modifier.alpha(backButtonVisibility) ){ Text("Back") }
                    Button(onClick = {
                        itemsViewModel.loadItems()
                    },
                        modifier = Modifier.alpha(refreshButtonVisibility) ){ Text("Refresh") }
                    Text(if (networkConnectivity== ConnectionState.Available) "Online " else "Offline ")
                    Text("•",
                        color= if (networkConnectivity== ConnectionState.Available) Color.Green else Color.Red,
                        modifier= Modifier.padding(end=20.dp))
                }
            )
        },
    ){
        if (selectedSender != null) {
            // alegem sender, afisam `ItemListFilteredBySender`
            ItemListFilteredBySender(
                mesajeList = (itemsUiState as Result.Success<List<Mesaj>>).data,
                selectedSender = selectedSender!!,
                modifier = Modifier.padding(it)
            )
        } else{
            when (itemsUiState) {
                is Result.Success ->
                    ItemListMapped(
                        mesajeList = (itemsUiState as Result.Success<List<Mesaj>>).data,
                        onItemClick = {
                                      selectedSender = it
                            Log.d("SCREEN", "selectedSender = ${selectedSender}  sender clicked = ${it}" )
//                            itemsViewModel.updateMessageFromSender(it)
//                            for(mesaj in itemsViewModel.updateMessageFromSender(selectedSender!!))
//                            {
//                                itemsViewModel.update(mesaj)
//                            }
                        },
                        modifier = Modifier.padding(it)
                    )

                is Result.Loading -> CircularProgressIndicator(modifier = Modifier.padding(it))
                is Result.Error -> Text(
                    text = "Failed to load items - ${(itemsUiState as Result.Error).exception?.message}",
                    modifier = Modifier.padding(it)
                )
            }
        }
    }
}