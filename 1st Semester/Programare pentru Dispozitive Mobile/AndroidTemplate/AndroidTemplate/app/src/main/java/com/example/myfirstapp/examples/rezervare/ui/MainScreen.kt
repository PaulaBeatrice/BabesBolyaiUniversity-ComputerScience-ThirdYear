package com.example.myfirstapp.examples.rezervare.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.example.myfirstapp.base.core.connectivityState
import com.example.myfirstapp.base.ui.Sys.Companion.makeToast
import com.example.myfirstapp.base.ui.screens.SimpleOptionScreen
import com.example.myfirstapp.examples.rezervare.app.Actions
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun MainScreen(onClose: (Int)->Unit) {
    val networkConnectivity by connectivityState()
    val context = LocalContext.current

    SimpleOptionScreen(
        onClose,
        mapOf("Secretar" to Actions.secretarSelected, "Pacient" to Actions.patientSelected),
        onPreviewSelected = {
            if(it==Actions.patientSelected && networkConnectivity.isUnavailable()) {
                context.makeToast("OFFLINE!")
                return@SimpleOptionScreen false
            }
            return@SimpleOptionScreen true
        }
    )
}