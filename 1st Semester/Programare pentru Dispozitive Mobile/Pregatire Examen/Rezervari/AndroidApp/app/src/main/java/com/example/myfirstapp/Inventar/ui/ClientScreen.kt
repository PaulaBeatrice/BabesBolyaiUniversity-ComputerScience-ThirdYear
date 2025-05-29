package com.example.myfirstapp.Inventar.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import com.example.myfirstapp.Inventar.MyFirstApplication
import com.example.myfirstapp.Inventar.core.ConnectionState
import com.example.myfirstapp.Inventar.core.PendingWorker
import com.example.myfirstapp.Inventar.core.TAG
import com.example.myfirstapp.Inventar.core.connectivityState
import com.example.myfirstapp.R
import com.example.myfirstapp.Inventar.core.Result
import com.example.myfirstapp.Inventar.domain.Produs


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientScreen(onClose: () -> Unit){
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


    LaunchedEffect(networkConnectivity) {
        if(isOffline) {
            Log.d(TAG, "AICI")
//            onClose()
//            if (networkConnectivity == ConnectionState.Available) {
//                val request = OneTimeWorkRequestBuilder<PendingWorker>().build()
//                workManager.enqueue(request)
//            }
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

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.magazin)) },
                actions = {
                    Button(onClick = onClose) { Text("Exit") }
                }
            )
        },
    ){
        when (itemsUiState) {
            is Result.Success ->
                FilteredItemList(
                    produsList = (itemsUiState as Result.Success<List<Produs>>).data,
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