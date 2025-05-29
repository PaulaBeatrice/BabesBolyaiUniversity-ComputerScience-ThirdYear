package com.example.myfirstapp.orders1.ui

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.util.query
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myfirstapp.R
import com.example.myfirstapp.orders1.MyFirstApplication
import com.example.myfirstapp.orders1.core.ConnectionState
import com.example.myfirstapp.orders1.core.PendingWorker
import com.example.myfirstapp.orders1.core.TAG
import com.example.myfirstapp.orders1.core.Result
import com.example.myfirstapp.orders1.core.connectivityState
import com.example.myfirstapp.orders1.domeniu.OrderItem
import com.example.myfirstapp.orders1.service.ConfirmRequest
import androidx.compose.material3.LinearProgressIndicator



@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onItemClick: (code: Int?) -> Unit) {
    Log.d("ItemsScreen", "Recompose")
    val itemsViewModel = viewModel<ItemsViewModel>(factory = ItemsViewModel.Factory)
    val itemsUiState by itemsViewModel.uiState.collectAsStateWithLifecycle()

    val app = LocalContext.current.applicationContext as MyFirstApplication
    val workManager = WorkManager.getInstance(app)
    val networkConnectivity by connectivityState()

    var isOffline by rememberSaveable { mutableStateOf(true) }
    var loaded by rememberSaveable { mutableStateOf(false) }
    val repository = LocalContext.current.applicationContext as MyFirstApplication

    LaunchedEffect(networkConnectivity) {
        if (isOffline) {
            if (networkConnectivity == ConnectionState.Available) {
                val request = OneTimeWorkRequestBuilder<PendingWorker>().build()
                workManager.enqueue(request)
            }
        }
        isOffline = networkConnectivity == ConnectionState.Unavailable
    }

    LaunchedEffect(true) {
        Log.d(TAG, "loaded = true")
        loaded = true
    }

    var itemsList =repository.container.itemRepository.getItems()

    val service = app.container.service;

    var updatedCount by rememberSaveable { mutableStateOf(0) }
    var isUpdating by rememberSaveable { mutableStateOf(false) }
    var showOnlyNonZeroQuantity by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(showOnlyNonZeroQuantity){
        if(showOnlyNonZeroQuantity){
            itemsList = repository.container.itemRepository.getItems()
        }
        else
            itemsList = repository.container.itemRepository.getItems().filter { item -> item.quantity != 0 }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.main_page)) },
                actions = {
                    Text(if (networkConnectivity == ConnectionState.Available) "Online " else "Offline ")
                    Text(
                        "•",
                        color = if (networkConnectivity == ConnectionState.Available) Color.Green else Color.Red,
                        modifier = Modifier.padding(end = 10.dp)
                    )
                }
            )
        })
        {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "")
                Text(text = "")
                Checkbox(
                    checked = showOnlyNonZeroQuantity,
                    onCheckedChange = { checked ->
                        showOnlyNonZeroQuantity = checked
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                SubmitButton(
                    itemsList = itemsList,
                    onUpdatedCountChanged = { count ->
                        updatedCount = count
                    },
                    onUpdateStart = { isUpdating = true },
                    onUpdateComplete = { isUpdating = false }
                )

//                Text("Updated count: $updatedCount", modifier = Modifier.padding(top = 8.dp))

                if (isUpdating) {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    )
                }
                ItemList(
                    orderItem = itemsList,
                    onItemClick = { it -> Log.d(TAG,"click ${it}")
                    },
                    modifier = Modifier.padding(16.dp)
                )


            }
        }
}

@Composable
fun SubmitButton(
    itemsList: List<OrderItem>,
    onUpdatedCountChanged: (Int) -> Unit,
    onUpdateStart: () -> Unit,
    onUpdateComplete: () -> Unit
) {
    val app = LocalContext.current.applicationContext as MyFirstApplication
    val service = app.container.service

    Button(
        onClick = {
            onUpdateStart()

            itemsList.filter { it.quantity != 0 }.forEach { item ->
                val updated = ConfirmRequest(
                    code = item.code,
                    quantity = item.quantity
                )
                service.submitItem(updated)
            }

            val count = itemsList.count { it.quantity != 0 }
            onUpdatedCountChanged(count)

            onUpdateComplete()
        },
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text("Submit")
    }
}


//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MainScreen(onItemClick: (code: Int?) -> Unit){
//    Log.d("ItemsScreen", "Recompose")
//    val itemsViewModel = viewModel<ItemsViewModel>(factory = ItemsViewModel.Factory)
//    val itemsUiState by itemsViewModel.uiState.collectAsStateWithLifecycle()
//
//    //var isOnline by rememberSaveable { mutableStateOf(false) }
//
//    val app = LocalContext.current.applicationContext as MyFirstApplication;
//    val service = app.container.service;
//    val repository = LocalContext.current.applicationContext as MyFirstApplication
//    var itemsList = repository.container.itemRepository.getItems()
//    val workManager = WorkManager.getInstance(app)
//    var isOffline by rememberSaveable { mutableStateOf(true) }
//    val networkConnectivity by connectivityState()
//    var refreshButtonVisibility by rememberSaveable {
//        mutableStateOf(0.0f)
//    }
//
//
//    LaunchedEffect(networkConnectivity) {
//        if(isOffline) {
//            if (networkConnectivity == ConnectionState.Available) {
//                val request = OneTimeWorkRequestBuilder<PendingWorker>().build()
//                workManager.enqueue(request)
//            }
//        }
//        isOffline = networkConnectivity == ConnectionState.Unavailable
//    }
//
//    var loaded by rememberSaveable {
//        mutableStateOf(false)
//    }
//
//    LaunchedEffect( true){
//        Log.d(TAG,"loaded = true")
//        loaded = true
//    }
//
//    Scaffold (
//        topBar = {
//            TopAppBar(
//                title = { Text(text = stringResource(id = R.string.main_page)) },
//                actions = {
//                    Text(if (networkConnectivity== ConnectionState.Available) "Online " else "Offline ")
//                    Text("•",
//                        color= if (networkConnectivity== ConnectionState.Available) Color.Green else Color.Red,
//                        modifier= Modifier.padding(end=20.dp))
////                    Button(onClick = onClose) { Text("Exit") }
//                }
//            )
//        },
//    ){
//        ItemList(
//            orderItem = itemsList ?: emptyList(),
//            onItemClick = { /* Handle item click if needed */ },
//            modifier = Modifier.padding(it)
//        )
//    }
//}