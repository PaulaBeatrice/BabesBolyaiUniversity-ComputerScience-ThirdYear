package com.example.myfirstapp.orders1.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstapp.orders1.domeniu.OrderItem
import com.example.myfirstapp.ui.theme.LocalCustomColorsPalette
import androidx.compose.material3.TextField
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.material3.Text


typealias OnItemFn = (item: OrderItem) -> Unit

@Composable
fun ItemList(orderItem: List<OrderItem>, onItemClick: OnItemFn, modifier: Modifier) {
    Log.d("ItemList", "recompose")
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(orderItem) { item ->
            ItemDetail(item, onItemClick)
        }
    }
}
@Composable
fun ItemDetail(item: OrderItem, onItemClick: OnItemFn) {
    Log.d("ItemDetail", "recompose code = ${item.code}")
    Row {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onItemClick(item) }
        ) {
            Text(
                text = "nume = ${item.nume}",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            )

            // Display editable quantity
            EditableQuantityField(item = item) { newQuantity ->
                // Handle the updated quantity here, you might want to update the item or perform other actions
                // For simplicity, just logging it for now
                Log.d("ItemDetail", "Updated quantity: $newQuantity")
            }
        }
    }
}

@Composable
fun EditableQuantityField(item: OrderItem, onQuantityChanged: (Int) -> Unit) {
    // Assuming that `item.quantity` is an Int, modify accordingly
    var quantityText by rememberSaveable { mutableStateOf(item.quantity.toString()) }

    TextField(
        value = quantityText,
        onValueChange = {
            quantityText = it

            // Convert the input text to an Int and notify the listener
            val newQuantity = it.toIntOrNull() ?: item.quantity
            onQuantityChanged(newQuantity)
        },
        label = { Text("Cantitate") },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        textStyle = TextStyle(fontSize = 18.sp),
    )
}
