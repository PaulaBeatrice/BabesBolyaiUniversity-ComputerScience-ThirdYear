package com.example.myfirstapp.examples.ex1_messages.app

import androidx.compose.runtime.Composable
import com.example.myfirstapp.base.NavGraph
import com.example.myfirstapp.examples.ex1_messages.ui.MainScreen

class Routes{
    companion object{
        val mainRoute = "main"
        val secretarRoute = "secretar"
        val confirmRoute = "confirm"
        val pacientRoute="pacient"
    }
}

class Actions{
    companion object{
        val noAction = 0;
        val secretarSelected:Int = 1
        val patientSelected:Int = 0
        val itemClick:Int = 1
    }
}

@Composable
fun AppNavHost(){
    NavGraph(
        nodes = mapOf(
            Routes.mainRoute to { MainScreen(onClose=it) },
        ),
        links = mapOf(
            //(Routes.mainRoute to Actions.patientSelected) to Routes.pacientRoute,
        ),
        startRoute = Routes.mainRoute
    ).createNavHost()
}