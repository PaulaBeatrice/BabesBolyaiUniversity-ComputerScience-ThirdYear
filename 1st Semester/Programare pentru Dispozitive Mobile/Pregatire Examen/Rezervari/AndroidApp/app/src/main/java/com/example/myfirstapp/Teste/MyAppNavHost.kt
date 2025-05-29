package com.example.myfirstapp.Teste

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myfirstapp.Teste.domain.Question
import com.example.myfirstapp.Teste.ui.MainScreen
import com.example.myfirstapp.Teste.ui.QuestionScreen

val mainRoute = "main"
val questionsRoute = "question"

@Composable
fun MyAppNavHost (){
    val navController = rememberNavController()
    val onCloseItem = {
        Log.d("MyAppNavHost", "navigate back to list")
        navController.popBackStack()
    }
    val myAppViewModel = viewModel<MyAppViewModel>(factory = MyAppViewModel.Factory)
    var questionList: List<Question> = emptyList()

    NavHost(
        navController = navController,
        startDestination = mainRoute
    ) {
        composable(route = mainRoute) {
            MainScreen (
//                id ->
//            Log.d("MyAppNavHost", "Received ID: $id")
            onClose = {
                    questions ->
                Log.d("GATA", "Intrebarile au fost incarcate -> Al doilea screen")
                questionList = questions
                navController.navigate(questionsRoute)
                 }
            )
        }

        composable(route = questionsRoute){
            QuestionScreen(questionList)
        }

    }
}
