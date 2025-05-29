package com.example.myfirstapp.Rezervari_Android.ui

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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.orders1.core.TAG
import com.example.myfirstapp.R
import com.example.myfirstapp.orders1.core.ConnectionState
import com.example.myfirstapp.orders1.core.connectivityState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onClose: (isPacient: Boolean)->Unit) {
    var isClicked by remember{ mutableStateOf(false)}
    var isPacient by remember{ mutableStateOf(false)}
    var isOffline by rememberSaveable { mutableStateOf(true) }
    val networkConnectivity by connectivityState()

    var context = LocalContext.current

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = stringResource(id = R.string.main_page)) }) },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(30.dp)
        ) {

            Button(onClick = {
                Log.d(TAG, "secretar...");
                isClicked = true;
                isPacient = false;

            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Secretar")
            }

            Button(onClick = {
                Log.d(TAG, "pacient...");
                if(networkConnectivity == ConnectionState.Available){
                    isClicked = true;
                    isPacient = true;
                }
                else{
                    Handler(Looper.getMainLooper()).post({
                        Toast.makeText(context, "OFFLINE!", Toast.LENGTH_LONG).show()
                    })
                }

            }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Pacient")
            }
        }
    }

    LaunchedEffect(isClicked) {
        Log.d(TAG, "Exit main");
        if (isClicked) {
            onClose(isPacient);
        }
    }
}