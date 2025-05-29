package com.example.myfirstapp.base.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.work.OneTimeWorkRequestBuilder
import com.example.myfirstapp.base.app.PendingWorker
import com.example.myfirstapp.base.core.ConnectionState
import com.example.myfirstapp.base.core.Result
import com.example.myfirstapp.base.core.connectivityState
import com.example.myfirstapp.base.ui.Callback
import com.example.myfirstapp.base.ui.ComposableComponent
import com.example.myfirstapp.base.ui.ComposableComponentWithPadding
import com.example.myfirstapp.base.ui.OnValueChanged
import com.example.myfirstapp.base.ui.components.NetworkIndicator
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
@Composable
fun GenericScreen(
    title: String,
    actionButtons:Map<String, ()->Unit> = mapOf(),
    topBar: ComposableComponent = { },
    useScroll:Boolean =false,
    networkStateChanged:OnValueChanged<ConnectionState>? = null,
    networkReconnected: Callback? = null,
    content: ComposableComponentWithPadding
){
    Log.d("GenericScreen", "Recompose")

    var networkIndicator: ComposableComponent = {}

    if(networkStateChanged!=null || networkReconnected!=null){
        val networkConnectivity by connectivityState()
        var isOffline by rememberSaveable { mutableStateOf(true) }

        networkIndicator = { NetworkIndicator(networkConnectivity = networkConnectivity)}

        LaunchedEffect(networkConnectivity) {
            if(networkStateChanged!=null)
                networkStateChanged(networkConnectivity)

            if(isOffline) {
                if (networkConnectivity == ConnectionState.Available) {
                    if(networkReconnected!=null)
                        networkReconnected()
                }
            }
            isOffline = networkConnectivity == ConnectionState.Unavailable
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(title = { Text(text = title) },
                actions = {
                    for(kv in actionButtons){
                        Button(onClick = { kv.value()}) { Text(kv.key) }
                    }
                    topBar()
                    networkIndicator()
                })
        }
    ) {
        if(useScroll) {
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .padding(it)
                    .fillMaxSize()
                    .verticalScroll(state = scrollState)
            ) {
                content(it)
            }
        }
        else{
            content(it)
        }
    }
}