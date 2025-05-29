package com.example.myfirstapp.examples.rezervare.ui

import androidx.compose.runtime.Composable
import com.example.myfirstapp.examples.rezervare.entity.Rezervare
import com.example.myfirstapp.base.ui.screens.ItemScreen


@Composable
fun PacientScreen (onClose: (Int) -> Unit){
    ItemScreen<Rezervare, Int>(
        Rezervare::class,
        null,
        onClose={ onClose(0) },
        visibleProps =  arrayOf("nume", "doctor", "data", "ora", "detalii"))
}
