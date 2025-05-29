package com.example.myfirstapp.Inventar.ui

import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.CheckBox
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.Inventar.MyFirstApplication
import com.example.myfirstapp.Inventar.core.ConnectionState
import com.example.myfirstapp.Inventar.core.Result
import com.example.myfirstapp.Inventar.core.connectivityState


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProdusAddScreen(onClose: () -> Unit){
    val itemViewModel = viewModel<ItemViewModel>(factory = ItemViewModel.Factory(null))
    val itemUiState = itemViewModel.uiState
    val app = LocalContext.current.applicationContext as MyFirstApplication;
    val context = LocalContext.current

    val networkConnectivity by connectivityState()

    var nume by rememberSaveable { mutableStateOf("") }
    var tip by rememberSaveable { mutableStateOf("") }
    var cantitate by rememberSaveable { mutableStateOf(0) }
    var pret by rememberSaveable { mutableStateOf(0) }
    var discount by rememberSaveable { mutableStateOf(0) }
    var status by rememberSaveable { mutableStateOf(false) }

    Log.d("ItemAddScreen", "recompose, text = $nume")

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
            TopAppBar(title = { Text(text = "Add Produs") },
                actions = {
                    Button(onClick = {
                        Log.d("ItemScreen", "back to list");
                        onClose()
                    }) { Text("Back") }

                    Button(onClick = {
                        Log.d("ItemScreen", "save item nume = $nume");
                        itemViewModel.saveItem(nume, tip, cantitate, pret, discount, status)
                        if(networkConnectivity == ConnectionState.Available)
                            Handler(Looper.getMainLooper()).post({
                                Toast.makeText(context, "S-a adaugat un produs nou!", Toast.LENGTH_LONG).show()
                            })
                    }, modifier=Modifier.padding(horizontal=8.dp)) { Text("Save") }
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
                    value = tip,
                    onValueChange = { tip = it }, label = { Text("Tip") },
                    modifier = defaultFieldModifier.fillMaxWidth(),
                )

                MyNumberField(
                    value = cantitate,
                    onValueChanged = {cantitate=it},
                    modifier = defaultFieldModifier,
                    label = "Cantitate"
                )

                MyNumberField(
                    value = pret,
                    onValueChanged = {pret=it},
                    modifier = defaultFieldModifier,
                    label = "Pret"
                )

                MyNumberField(
                    value = discount,
                    onValueChanged = {discount=it},
                    modifier = defaultFieldModifier,
                    label = "Discount"
                )

                Row(verticalAlignment = Alignment.CenterVertically, modifier = defaultFieldModifier) {
                    Checkbox(
                        checked = status,
                        onCheckedChange = { status = it }
                    )
                    Text(
                        text="Status",
                    )
                }
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



