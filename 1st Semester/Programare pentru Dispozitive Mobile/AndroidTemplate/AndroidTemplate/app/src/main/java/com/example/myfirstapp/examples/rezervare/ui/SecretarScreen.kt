package com.example.myfirstapp.examples.rezervare.ui

import android.app.Application
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myfirstapp.base.app.PendingWorker
import com.example.myfirstapp.base.core.Result
import com.example.myfirstapp.base.items.*
import com.example.myfirstapp.base.ui.components.*
import com.example.myfirstapp.base.ui.screens.*
import com.example.myfirstapp.examples.rezervare.entity.Rezervare

@Composable
fun SecretarScreen(onClose: (id: Int) -> Unit){
    Log.d("SecretarScreen", "Recompose")
    val itemsViewModel = createItemsViewModel<Rezervare, Int>()

    val app = LocalContext.current.applicationContext as Application;
    val workManager = WorkManager.getInstance(app)

    var refreshButtonVisibility by rememberSaveable { mutableStateOf(0.0f) }

    GenericScreen(
        title="Rezervari",
        topBar = {
            Button(onClick = {
                itemsViewModel.loadItems()
            }, modifier = Modifier.alpha(refreshButtonVisibility) ){ Text("Refresh")}
            Button(onClick = {onClose(0)}) { Text("Exit") }
        },
        networkReconnected = {
            val request = OneTimeWorkRequestBuilder<PendingWorker<Rezervare>>().build()
            workManager.enqueue(request)
        }
    ){
        ItemsListWithViewModel<Rezervare, Int>(
            Rezervare::class,
            itemsViewModel,
            onItemClick = { itemsViewModel.update(it); onClose(1) },
            sortKey = { "${it.doctor},${it.data}, ${it.ora}" },
            filter = { it.status==false || it.isUpdated == false },
            itemsUiStateChanged = {
                val cnt = if(it !is Result.Success<*>) 0 else
                (it as Result.Success<List<Rezervare>>).data.size
                refreshButtonVisibility = if(cnt==0) 1.0f else 0.0f
            },
            modifier = Modifier.padding(it)
        )
    }
}