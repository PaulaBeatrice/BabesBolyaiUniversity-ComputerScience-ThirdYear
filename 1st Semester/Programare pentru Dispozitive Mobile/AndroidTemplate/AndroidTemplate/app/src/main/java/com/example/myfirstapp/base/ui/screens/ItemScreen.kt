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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.myfirstapp.base.items.Item
import kotlin.reflect.KClass
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfirstapp.base.app.ItemController
import com.example.myfirstapp.base.core.Result
import com.example.myfirstapp.base.items.ItemDefaults
import com.example.myfirstapp.base.items.ItemViewModel
import com.example.myfirstapp.base.ui.OnCloseFun
import com.example.myfirstapp.base.ui.Sys
import com.example.myfirstapp.base.ui.Sys.Companion.makeToast
import com.example.myfirstapp.base.ui.components.ItemEditorFields
import kotlin.reflect.full.createInstance


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T: Item, ID> ItemScreen(
    itemClass: KClass<T>,
    item:T?,
    getId: (T)->ID = ItemDefaults.defaultGetItemId(itemClass),
    copyItem: (T)->T = ItemDefaults.defaultCopyItemFunc(itemClass),
    onClose: OnCloseFun={},
    visibleProps:Array<String>?=null,
) {
    val context = LocalContext.current

    val itemViewModel = viewModel<ItemViewModel<T, ID>>(factory = ItemViewModel.Factory<T,ID>(
        itemClass,
        if(item==null) null else getId(item),
        getId,
        copyItem,
    ))
    val itemUiState = itemViewModel.uiState

    LaunchedEffect(itemUiState.submitResult) {
        Log.d("ItemScreen", "Submit = ${itemUiState.submitResult}");
        if (itemUiState.submitResult is Result.Success) {
            Log.d("ItemScreen", "Closing screen");
            onClose(0);
        }
    }

    val defaultFieldModifier = Modifier.padding(vertical = 5.dp)
    var editedItem by remember{ mutableStateOf(itemClass.createInstance()) }

    Scaffold (
        topBar = {
            TopAppBar(title = { Text(text = "Add Rezervare") },
                actions = {
                    Button(onClick = {
                        Log.d("ItemScreen", "back to list");
                        onClose(0)
                    }) { Text("Back") }

                    Button(onClick = {
                        Log.d("ItemScreen", "save item $editedItem");
                        itemViewModel.saveItem(editedItem);
                        context.makeToast("S-a adaugat o rezervare noua!")
                    }, modifier= Modifier.padding(horizontal=8.dp)) { Text("Save") }
                })
        }
    ) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .verticalScroll(state = scrollState)
        ) {
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
            Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 0.dp)) {
                ItemEditorFields(
                    itemClass,
                    itemClass.createInstance(),
                    itemEdited = { editedItem = it },
                    visibleProps=visibleProps)
            }
        }
    }
}