package com.ilazar.myapp.todo.ui

import android.app.DatePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.ilazar.myapp.R
import com.ilazar.myapp.todo.data.getCurrentDate
import com.ilazar.myapp.todo.ui.item.ItemViewModel
import com.ilazar.myapp.ui.TAG
import com.ilazar.myservices.util.createNotificationChannel
import com.ilazar.myservices.util.showSimpleNotification
import java.util.*

@Composable
fun ItemScreen(itemId: String?, onClose: () -> Unit) {
    val localContext = LocalContext.current
    val itemViewModel = viewModel<ItemViewModel>(factory = ItemViewModel.Factory(itemId))
    val itemUiState = itemViewModel.uiState
    var sold by rememberSaveable { mutableStateOf(itemUiState.item?.sold ?: false) }
    var price by rememberSaveable { mutableStateOf(itemUiState.item?.price ?: 0) }
    var title by rememberSaveable { mutableStateOf(itemUiState.item?.title ?: "") }

    var lat by rememberSaveable { mutableStateOf(itemUiState.item?.lat ?: 46.0) }
    var lng by rememberSaveable { mutableStateOf(itemUiState.item?.lng ?: 23.0) }
    Log.d("ItemScreen", "recompose, text = $title")

    val context = LocalContext.current
    val channelId = "MyTestChannel"
    val notificationId = 0

    LaunchedEffect(Unit) {
        createNotificationChannel(channelId, context)
    }

    LaunchedEffect(itemUiState.savingCompleted) {
        Log.d("ItemScreen", "Saving completed = ${itemUiState.savingCompleted}");
        if (itemUiState.savingCompleted) {
            onClose();
        }
    }

    val markerState = rememberMarkerState(position = LatLng(lat, lng))
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
    }

    var textInitialized by remember { mutableStateOf(itemId == null) }
    LaunchedEffect(itemId, itemUiState.isLoading) {
        Log.d("ItemScreen", "Saving completed = ${itemUiState.savingCompleted}");
        if (textInitialized) {
            return@LaunchedEffect
        }
        if (itemUiState.item != null && !itemUiState.isLoading) {
            sold = itemUiState.item.sold
            price = itemUiState.item.price
            title = itemUiState.item.title
            lat = itemUiState.item.lat
            lng = itemUiState.item.lng
            markerState.position = LatLng(lat,lng)
            cameraPositionState.position=CameraPosition.fromLatLngZoom(markerState.position, 10f)
            Log.d("Location","${markerState.position}")
            textInitialized = true
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.item)) },
                actions = {
                    Button(onClick = {
                        Log.d("ItemScreen", "save item text = $title");
                        val date = Date().toString()
                        itemViewModel.saveOrUpdateItem(date, sold, price, title,lat,lng)
                        showSimpleNotification(
                            context,
                            channelId,
                            notificationId,
                            "Saved succesfully!!!",
                            "Added book with title = $title"
                        )
                    }) { Text("Save") }
                }
            )
        }
    ) {
        if (itemUiState.isLoading) {
            CircularProgressIndicator()
            return@Scaffold
        }
        Column {
        if (itemUiState.loadingError != null) {
            Text(text = "Failed to load item - ${itemUiState.loadingError.message}")
        }
            Row {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row {
                TextField(
                    value = price.toString(),
                    onValueChange = { price = it.toIntOrNull() ?: 0 },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            Row {
                Checkbox(
                    checked = sold,
                    onCheckedChange = { sold = it },
                    modifier = Modifier.padding(16.dp)
                )
                Text("Sold")
            }
        Row{
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = {
                    Log.d(TAG, "onMapClick $it")
                },
                onMapLongClick = {
                    Log.d(TAG, "onMapLongClick $it")
                    markerState.position = it
                    lat=it.latitude
                    lng=it.longitude
                },
            ) {
                Marker(
                    state = markerState,
                    title = "User location title",
                    snippet = "User location",
                )
            }
        }
        if (itemUiState.isSaving) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) { LinearProgressIndicator() }
        }
        if (itemUiState.savingError != null) {
            Text(text = "Failed to save item - ${itemUiState.savingError.message}")
        }
        }
    }
}

@Preview
@Composable
fun PreviewItemScreen() {
    ItemScreen(itemId = "0", onClose = {})
}
