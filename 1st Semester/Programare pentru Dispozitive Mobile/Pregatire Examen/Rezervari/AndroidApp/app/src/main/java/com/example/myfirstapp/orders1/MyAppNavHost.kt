package com.example.myfirstapp.orders1

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapp.orders1.ui.MainScreen

val mainRoute = "main"


@Composable
fun MyAppNavHost (){
    val navController = rememberNavController()
    val onCloseItem = {
        Log.d("MyAppNavHost", "navigate back to list")
        navController.popBackStack()
    }
    val myAppViewModel = viewModel<MyAppViewModel>(factory = MyAppViewModel.Factory)
    NavHost(
        navController = navController,
        startDestination = mainRoute
    ) {
        composable(route = mainRoute)
        {
            MainScreen(
                onItemClick = {}
            )
        }

    }
}
