package com.example.myfirstapp.Rezervari_Android.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstapp.Rezervari_Android.domain.Rezervare
import com.example.myfirstapp.ui.theme.LocalCustomColorsPalette

typealias OnItemFn = (rezervare: Rezervare) -> Unit

@Composable
fun ItemList(rezervareList: List<Rezervare>, onItemClick: OnItemFn, modifier: Modifier) {
    Log.d("ItemList", "recompose")
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(rezervareList.sortedBy { it -> "${it.doctor},${it.data}, ${it.ora}" }.filter { it -> it.status==false || it.isUpdated == false }) { item ->
            ItemDetail(item, onItemClick)
        }
    }
}

@Composable
fun ItemDetail(rezervare: Rezervare, onItemClick: OnItemFn) {
    Log.d("ItemDetail", "recompose id = ${rezervare.id}, nume: ${rezervare.nume} , update : ${rezervare.isUpdated}")
    Row {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable { onItemClick(rezervare) }
        ) {
            Text(
                text = "id = ${rezervare.id}, nume: ${rezervare.nume}, doctor: ${rezervare.doctor}, data: ${rezervare.data}, ora: ${rezervare.ora}, status: ${rezervare.status} , update : ${rezervare.isUpdated} ",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if(!rezervare.isUpdated)
                    LocalCustomColorsPalette.current.pendingOp else Color.Unspecified
            )
        }
    }
}
