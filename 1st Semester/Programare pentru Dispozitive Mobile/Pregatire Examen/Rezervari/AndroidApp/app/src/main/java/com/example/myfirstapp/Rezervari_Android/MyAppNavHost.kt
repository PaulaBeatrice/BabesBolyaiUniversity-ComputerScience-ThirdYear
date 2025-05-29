package com.example.myfirstapp.Rezervari_Android

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapp.orders1.core.TAG
import com.example.myfirstapp.Rezervari_Android.ui.ConfirmScreen
import com.example.myfirstapp.Rezervari_Android.ui.MainScreen
import com.example.myfirstapp.Rezervari_Android.ui.PacientScreen
import com.example.myfirstapp.Rezervari_Android.ui.SecretarScreen


val mainRoute = "main"
val secretarRoute = "secretar"
val confirmRoute = "confirm"
val pacientRoute="pacient"

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
                onClose = {
                    isPacient -> Log.d(TAG, isPacient.toString())
                    if(!isPacient){
                        navController.navigate(secretarRoute)
                    }
                    else
                        navController.navigate(pacientRoute)
//                    Log.d("MyAppNavHost", "navigate to list")
//                    navController.navigate(itemsRoute)
                }
            )
        }

        composable(route = secretarRoute)
        {
            SecretarScreen(
                onItemClick = {
                        id->
                            val it = id?:-1
                            Log.d(TAG,it.toString())

                        navController.navigate(confirmRoute)
                },
                onClose = {
                    navController.navigate(mainRoute)
                },


            )
        }

        composable(route = confirmRoute)
        {
            ConfirmScreen(
                onClose = {
                    navController.navigate(secretarRoute)
                }
                )
        }

        composable(route = pacientRoute)
        {
            PacientScreen(
                onClose = {
                    navController.navigate(mainRoute)
                }
            )
        }
    }
}