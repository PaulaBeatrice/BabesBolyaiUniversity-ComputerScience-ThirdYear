package com.example.myfirstapp.Mesaje.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myfirstapp.Mesaje.domain.Mesaj
import com.example.myfirstapp.ui.theme.LocalCustomColorsPalette
import com.example.myfirstapp.Mesaje.core.TAG
import com.example.myfirstapp.Rezervari_Android.ui.ItemDetail
import kotlinx.coroutines.delay

typealias OnItemFn = (sender: String) -> Unit

@Composable
fun ItemListMapped(mesajeList: List<Mesaj>, onItemClick: OnItemFn, modifier: Modifier) {
    Log.d("ItemList", "recompose")
    val sortedMesajeList = mesajeList.sortedWith(compareByDescending<Mesaj> {
        !it.read // First, sort by read (false comes first)
    }.thenByDescending {
        it.created // Then, sort by created in descending order
    })

    // Group messages by sender
    val groupedBySender = sortedMesajeList.groupBy { it.sender }

    // Calculate the number of unread messages for each sender
    val unreadCounts = groupedBySender.mapValues { entry ->
        entry.value.count { !it.read }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Display each sender and the number of unread messages
        items(unreadCounts.toList()) { (sender, unreadCount) ->
            Text(
                text = "Sender: $sender [ $unreadCount ]",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(sender) } // Handle click on sender
                    .padding(8.dp)
            )
        }
    }
}

fun onItemClick(sender: String) {
    Log.d("CLICK", "Am dat click pe ${sender}")
}

@Composable
fun ItemListFilteredBySender(mesajeList: List<Mesaj>, selectedSender:String, modifier: Modifier) {
    Log.d("ItemList", "recompose")
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        items(mesajeList.sortedBy { it -> "${it.created}" }.filter { it -> it.sender==selectedSender}) { item ->
            ItemDetail(item)
        }
    }
}

@Composable
fun ItemDetail(mesaj: Mesaj) {
    var isBold by remember { mutableStateOf(false) }

    LaunchedEffect(mesaj) {
        isBold = true
        delay(1000) // Așteptați timp de o secundă
        isBold = false
    }

    Log.d("ItemDetail", "recompose id = ${mesaj.id}, sender: ${mesaj.sender} , created : ${mesaj.created}")
    Row {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "id = ${mesaj.id}, text: ${mesaj.text}, created: ${mesaj.created},  update : ${mesaj.isUpdated} ",
                style = TextStyle(
                    fontSize = 24.sp,
                    fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
                ),
                color = if (!mesaj.isUpdated)
                    LocalCustomColorsPalette.current.pendingOp else Color.Unspecified
            )
        }
    }
}

