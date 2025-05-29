package com.example.myfirstapp.base.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable

typealias Callback = ()->Unit
typealias OnCloseFun = (Int)->Unit
typealias ComposableComponent = @Composable ()->Unit
typealias ComposableComponentWithPadding = @Composable (PaddingValues)->Unit
typealias OnValueChanged<T> = (T)->Unit
typealias OnValuesChanged<T1, T2> = (T1, T2)->Unit