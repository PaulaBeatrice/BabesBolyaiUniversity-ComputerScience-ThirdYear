package com.example.myfirstapp.examples.rezervare.app

import androidx.compose.runtime.Composable
import com.example.myfirstapp.base.NavGraph
import com.example.myfirstapp.base.ui.screens.EmptyScreen
import com.example.myfirstapp.examples.rezervare.ui.MainScreen
import com.example.myfirstapp.examples.rezervare.ui.PacientScreen
import com.example.myfirstapp.examples.rezervare.ui.SecretarScreen

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
            Routes.secretarRoute to { SecretarScreen(onClose=it) },
            Routes.confirmRoute to { EmptyScreen(onClose=it, exitCode=Actions.noAction) },
            Routes.pacientRoute to { PacientScreen(onClose=it) }
        ),
        links = mapOf(
            (Routes.mainRoute to Actions.patientSelected) to Routes.pacientRoute,
            (Routes.mainRoute to Actions.secretarSelected) to Routes.secretarRoute,
            (Routes.secretarRoute to Actions.itemClick) to Routes.confirmRoute,
            (Routes.secretarRoute to Actions.noAction) to Routes.mainRoute,
            (Routes.pacientRoute to Actions.noAction) to Routes.mainRoute,
            (Routes.confirmRoute to Actions.noAction) to Routes.secretarRoute
        ),
        startRoute = Routes.mainRoute
    ).createNavHost()
}