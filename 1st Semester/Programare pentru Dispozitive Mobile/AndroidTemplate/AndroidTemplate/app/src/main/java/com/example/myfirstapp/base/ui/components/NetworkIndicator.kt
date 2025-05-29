package com.example.myfirstapp.base.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.base.core.ConnectionState

@Composable
fun NetworkIndicator(networkConnectivity: ConnectionState) {
    Text(if (networkConnectivity== ConnectionState.Available) "Online " else "Offline ")
    Text("•",
        color= if (networkConnectivity== ConnectionState.Available) Color.Green else Color.Red,
        modifier= Modifier.padding(end=20.dp))
}