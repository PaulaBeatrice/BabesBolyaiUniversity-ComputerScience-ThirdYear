package com.example.myfirstapp.Inventar.ui

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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstapp.Inventar.domain.Produs
import com.example.myfirstapp.ui.theme.LocalCustomColorsPalette


@Composable
fun ItemList(produsList: List<Produs>, modifier: Modifier) {
    Log.d("ItemList", "recompose")
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(produsList) { item ->
            ItemDetail(item)
        }
    }
}

@Composable
fun FilteredItemList(produsList: List<Produs>, modifier: Modifier) {
    Log.d("ItemList", "recompose")

    // Funcție auxiliară pentru a obține cel mai ieftin produs din fiecare tip
    fun getCheapestProductsByType(produsList: List<Produs>): List<Produs> {
        val cheapestProducts = mutableMapOf<String, Produs>()

        for (produs in produsList) {
            if (!cheapestProducts.containsKey(produs.tip) || produs.pret < cheapestProducts[produs.tip]!!.pret) {
                cheapestProducts[produs.tip] = produs
            }
        }

        return cheapestProducts.values.toList()
    }

    val filteredList = getCheapestProductsByType(produsList)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(filteredList) { item ->
            ItemDetail(item)
        }
    }
}


@Composable
fun ItemDetail(produs: Produs) {
    Log.d("ItemDetail", "recompose id = ${produs.id}, nume: ${produs.nume} , update : ${produs.isUpdated}")
    Row {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "id = ${produs.id}, nume: ${produs.nume}, tip: ${produs.tip}, pret: ${produs.pret}, cantitate: ${produs.cantitate} , update : ${produs.isUpdated} ",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = if(!produs.isUpdated)
                    LocalCustomColorsPalette.current.pendingOp else Color.Unspecified
            )
        }
    }
}
