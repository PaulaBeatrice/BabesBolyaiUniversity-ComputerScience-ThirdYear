package com.example.myfirstapp.base

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class NavGraph(
    private val nodes:Map<String, @Composable (onClose:(Int)->Unit)->Unit>,
    private val links:Map<Pair<String, Int>, String>,
    private val startRoute:String){

    @Composable
    fun createNavHost():Unit{
        val navController = rememberNavController()

        NavHost(
            navController = navController,
            startDestination = startRoute
        ){
            for(kv in nodes){
                val route = kv.key
                val screen = kv.value
                composable(route = route)
                {
                    screen(
                        onClose = {
                            if(!links.containsKey(route to it))
                                throw Exception("NavGraph: No follow up from $route with code $it")
                            navController.navigate(links[route to it]!!)
                        }
                    )
                }
            }
        }
    }
}