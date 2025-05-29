package com.example.myfirstapp.Rezervari_Android.ui

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
import com.example.myfirstapp.R
import com.example.myfirstapp.Rezervari_Android.MyFirstApplication
import com.example.myfirstapp.Rezervari_Android.core.ConnectionState
import com.example.myfirstapp.Rezervari_Android.core.PendingWorker
import com.example.myfirstapp.Rezervari_Android.core.TAG
import com.example.myfirstapp.Rezervari_Android.core.Result
import com.example.myfirstapp.Rezervari_Android.core.connectivityState
import com.example.myfirstapp.Rezervari_Android.domain.Rezervare


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretarScreen(onItemClick: (id: Int?) -> Unit, onClose: () -> Unit){
    Log.d("ItemsScreen", "Recompose")
    val itemsViewModel = viewModel<ItemsViewModel>(factory = ItemsViewModel.Factory)
    val itemsUiState by itemsViewModel.uiState.collectAsStateWithLifecycle()

    //var isOnline by rememberSaveable { mutableStateOf(false) }

    val app = LocalContext.current.applicationContext as MyFirstApplication;
    val service = app.container.service;
    val workManager = WorkManager.getInstance(app)
    var isOffline by rememberSaveable { mutableStateOf(true) }
    val networkConnectivity by connectivityState()
    var refreshButtonVisibility by rememberSaveable {
        mutableStateOf(0.0f)
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

//    LaunchedEffect(networkConnectivity) {
//        val cnt = if(itemsUiState is Result.Loading) 0 else
//                    (itemsUiState as Result.Success<List<Rezervare>>).data.size
//        Log.d(TAG, "${itemsUiState}")
//        Log.d(TAG," launched effect - 2 ${networkConnectivity} , ${cnt}")
//        if (networkConnectivity == ConnectionState.Available) {
//            Log.d(TAG,"net = available")
//            if(cnt == 0)
//                refreshButtonVisibility = 1.0f
//            else
//                refreshButtonVisibility = 0.0f
//        }
//        else{
//            Log.d(TAG,"offline   ${cnt} X")
//            if(cnt  == 0)
//                refreshButtonVisibility = 1.0f
//            else
//                refreshButtonVisibility = 0.0f
//        }
//        isOffline = networkConnectivity == ConnectionState.Unavailable
//    }

    LaunchedEffect(itemsUiState){
        val cnt = if(itemsUiState is Result.Loading) 0 else
            (itemsUiState as Result.Success<List<Rezervare>>).data.size
        if(cnt == 0)
            refreshButtonVisibility = 1.0f
        else
            refreshButtonVisibility = 0.0f
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.secretar)) },
                actions = {
                    Button(onClick = {
                        itemsViewModel.loadItems()
                    },
                        modifier = Modifier.alpha(refreshButtonVisibility) ){ Text("Refresh")}
                    Text(if (networkConnectivity== ConnectionState.Available) "Online " else "Offline ")
                    Text("•",
                        color= if (networkConnectivity== ConnectionState.Available) Color.Green else Color.Red,
                        modifier= Modifier.padding(end=20.dp))
                    Button(onClick = onClose) { Text("Exit") }
                }
            )
        },
    ){
        when (itemsUiState) {
            is Result.Success<*> ->
                ItemList(
                    rezervareList = (itemsUiState as Result.Success<List<Rezervare>>).data,
                    onItemClick = {
                                    itemsViewModel.confirm(it);
                                    onItemClick(it.id)
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