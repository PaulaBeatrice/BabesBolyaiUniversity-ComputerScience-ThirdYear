package com.example.myfirstapp.Rezervari_Android.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfirstapp.Rezervari_Android.MyFirstApplication
import com.example.myfirstapp.orders1.core.Result



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PacientScreen (onClose: () -> Unit){
    val itemViewModel = viewModel<ItemViewModel>(factory = ItemViewModel.Factory(null))
    val itemUiState = itemViewModel.uiState
    val app = LocalContext.current.applicationContext as MyFirstApplication;
    val context = LocalContext.current

    var nume by rememberSaveable { mutableStateOf("") }
    var doctor by rememberSaveable { mutableStateOf("") }
    var data by rememberSaveable { mutableStateOf(0) }
    var ora by rememberSaveable { mutableStateOf(0) }
    var detalii by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(itemUiState.submitResult) {
        Log.d("ItemScreen", "Submit = ${itemUiState.submitResult}");
        if (itemUiState.submitResult is Result.Success) {
            Log.d("ItemScreen", "Closing screen");

            onClose();
        }
    }


    val defaultFieldModifier = Modifier.padding(vertical = 5.dp)

    Scaffold (
        topBar = {
            TopAppBar(title = { Text(text = "Add Rezervare") },
                actions = {
                    Button(onClick = {
                        Log.d("ItemScreen", "back to list");
                            onClose()
                    }) { Text("Back") }

                    Button(onClick = {
                        Log.d("ItemScreen", "save item text = $nume");
                        itemViewModel.saveItem(nume, doctor, data, ora, detalii, false);
                        Handler(Looper.getMainLooper()).post({
                            Toast.makeText(context, "S-a adaugat o rezervare noua!", Toast.LENGTH_LONG).show()
                        })
                    }, modifier= Modifier.padding(horizontal=8.dp)) { Text("Save") }
                })
        }
    )
    {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ){
            if (itemUiState.loadResult is Result.Loading) {
                CircularProgressIndicator()
                return@Scaffold
            }
            if (itemUiState.submitResult is Result.Loading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { LinearProgressIndicator() }
            }
            if (itemUiState.loadResult is Result.Error) {
                Text(text = "Failed to load item - ${(itemUiState.loadResult as Result.Error).exception?.message}")
            }
            Column(modifier = Modifier.padding(horizontal=24.dp,vertical=0.dp)) {
                TextField(
                    value = nume,
                    onValueChange = { nume = it }, label = { Text("Nume") },
                    modifier = defaultFieldModifier.fillMaxWidth(),
                )

                TextField(
                    value = doctor,
                    onValueChange = { doctor = it }, label = { Text("Doctor") },
                    modifier = defaultFieldModifier.fillMaxWidth(),
                )

                MyNumberField(
                    value = data,
                    onValueChanged = {data=it},
                    modifier = defaultFieldModifier,
                    label = "Data"
                )

                MyNumberField(
                    value = ora,
                    onValueChanged = {ora=it},
                    modifier = defaultFieldModifier,
                    label = "Ora"
                )

                TextField(
                    value = detalii,
                    onValueChange = { detalii = it }, label = { Text("Detalii") },
                    modifier = defaultFieldModifier.fillMaxWidth(),
                )
            }

            if (itemUiState.submitResult is Result.Error) {
                Text(
                    text = "Failed to submit item - ${(itemUiState.submitResult as Result.Error).exception?.message}",
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
