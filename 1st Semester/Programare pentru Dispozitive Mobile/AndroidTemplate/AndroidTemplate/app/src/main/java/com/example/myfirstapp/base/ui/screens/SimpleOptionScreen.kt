package com.example.myfirstapp.base.ui.screens

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.R
import com.example.myfirstapp.base.core.ConnectionState
import com.example.myfirstapp.base.core.TAG

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleOptionScreen(
    onClose:(Int)->Unit,
    options:Map<String, Int>,
    onPreviewSelected:(Int)->Boolean={true},
    title:String = "Main"
) {
    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = title) }) },
    ) {
        Column(
            modifier = Modifier.padding(it).fillMaxSize().padding(30.dp)
        ) {
            for(kv in options){
                Button(onClick = {
                    Log.d(TAG, "Clicked ${kv.value}")
                    if(onPreviewSelected(kv.value))
                        onClose(kv.value)

                }, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text(kv.key) }
            }
        }
    }
}