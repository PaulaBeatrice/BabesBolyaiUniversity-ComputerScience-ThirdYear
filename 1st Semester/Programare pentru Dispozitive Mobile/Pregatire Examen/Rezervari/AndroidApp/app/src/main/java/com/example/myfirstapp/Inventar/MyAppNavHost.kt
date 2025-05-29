package com.example.myfirstapp.Inventar

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapp.Inventar.core.TAG
import com.example.myfirstapp.Inventar.ui.MagazionerScreen
import com.example.myfirstapp.Inventar.ui.MainScreen
import com.example.myfirstapp.Inventar.core.TAG
import com.example.myfirstapp.Inventar.ui.ClientScreen
import com.example.myfirstapp.Inventar.ui.ProdusAddScreen
import com.example.myfirstapp.itemsRoute


val mainRoute = "main"
val magazionerRoute = "magazioner"
val clientRoute = "client"
val addProdusRoute = "produs"

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
                onClose = { isClient ->
                    Log.d(TAG, isClient.toString())
                    if (!isClient) {
                        navController.navigate(magazionerRoute)
                    } else
                        navController.navigate(clientRoute)
                }
            )
        }

        composable(route = magazionerRoute) {
            MagazionerScreen(
                onAddItem = {
                    Log.d(TAG,"CLICK PE ADD")
                    navController.navigate(addProdusRoute)
                },
                onClose = {
                    navController.navigate(mainRoute)
                }
            )
        }

        composable(route = addProdusRoute){
            ProdusAddScreen(
                onClose = {
                    navController.navigate(magazionerRoute)
                }
            )
        }

        composable(route = clientRoute){
            ClientScreen(
                onClose = {
                    navController.navigate(mainRoute)
                }
            )
        }
    }
}