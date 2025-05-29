package com.example.myfirstapp.base.ui.screens

import androidx.compose.runtime.Composable

@Composable
fun EmptyScreen(onClose:(Int)->Unit, exitCode:Int) {
    onClose(exitCode)
}