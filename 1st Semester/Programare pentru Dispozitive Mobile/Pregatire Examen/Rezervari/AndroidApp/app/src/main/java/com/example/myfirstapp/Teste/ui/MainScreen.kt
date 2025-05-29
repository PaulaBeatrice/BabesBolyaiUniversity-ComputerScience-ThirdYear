package com.example.myfirstapp.Teste.ui

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.myfirstapp.R
import com.example.myfirstapp.Teste.MyFirstApplication
import com.example.myfirstapp.Teste.core.ConnectionState
import com.example.myfirstapp.Teste.core.PendingWorker
import com.example.myfirstapp.Teste.core.TAG
import com.example.myfirstapp.Teste.core.connectivityState
import com.example.myfirstapp.Teste.domain.Question

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onClose: (List<Question>) -> Unit) {
    var isClicked by remember { mutableStateOf(false) }
    var id by remember { mutableStateOf(0) }

    val itemsViewModel = viewModel<ItemsViewModel>(factory = ItemsViewModel.Factory)
    val itemsUiState by itemsViewModel.uiState.collectAsStateWithLifecycle()

    val app = LocalContext.current.applicationContext as MyFirstApplication
    val service = app.container.service

    var context = LocalContext.current

    var progress by remember { mutableStateOf(0) }
    var totalQuestions by remember { mutableStateOf(0) }
    var visibility by remember { mutableStateOf(0.0f) }
    var retryVisibility by remember { mutableStateOf(0.0f) }

    // internet
    val workManager = WorkManager.getInstance(app)
    var isOffline by rememberSaveable { mutableStateOf(true) }
    val networkConnectivity by connectivityState()
//    var refreshButtonVisibility by rememberSaveable {
//        mutableStateOf(0.0f)
//    }

    var questionIds by rememberSaveable { mutableStateOf(emptyList<Int>()) }
    var allQuestions = mutableStateListOf<Question>()


    var getQuestions by rememberSaveable { mutableStateOf(false) } // retinem daca am descarcat id urile intrebarilor
    var retry by rememberSaveable {
        mutableStateOf(false)
    }


    LaunchedEffect(networkConnectivity) {
        if(isOffline) {
            if (networkConnectivity == ConnectionState.Available) {
                val request = OneTimeWorkRequestBuilder<PendingWorker>().build()
                workManager.enqueue(request)
            }
        }
        isOffline = networkConnectivity == ConnectionState.Unavailable
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.main_page)) },
                actions = {
                    Button(onClick = {
                        if (networkConnectivity == ConnectionState.Available) {
                            retry = true
                            isClicked = false
                            retryVisibility = 0.0f
                        } else {
                            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
                            retryVisibility = 1.0f
                        }

//                        if (networkConnectivity == ConnectionState.Available) {
//                            isClicked = true
//                        } else {
//                            Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
//                            retryVisibility = 1.0f
//                        }

                    },
                        modifier = Modifier.alpha(retryVisibility)) {
                        Text("Retry")
                    }
                    Text(
                        if (networkConnectivity == ConnectionState.Available)
                            "Online "
                        else
                            "Offline "
                    )
                    Text(
                        "•",
                        color = if (networkConnectivity == ConnectionState.Available) Color.Green else Color.Red,
                        modifier = Modifier.padding(end = 20.dp)
                    )
                }
            )
        }) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(30.dp)
        ) {
            var textValue by rememberSaveable { mutableStateOf("") }

            TextField(
                value = textValue,
                onValueChange = {
                    textValue = it
                    id = it.toIntOrNull() ?: 0
                },
                label = { Text("Enter ID") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = {
                    Log.d(TAG, "Get questions...")

                    if (networkConnectivity == ConnectionState.Available) {
                        isClicked = true
                    } else {
                        Toast.makeText(context, "No internet connection", Toast.LENGTH_LONG).show()
                        retryVisibility = 1.0f
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Get Questions")
            }



            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .alpha(visibility),
                progress = progress.toFloat() / totalQuestions
            )

            // Text to display the progress
            Text(
                text = "Downloading $progress/$totalQuestions",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .alpha(visibility)
            )
        }
    }

    LaunchedEffect(isClicked, isOffline) {
        if (isClicked) {
            visibility = 1.0f


            // Call getQuestionIds
            Log.d(TAG, "ID TRIMIS $id")
            questionIds = service.getQuestionIds(id).questionIds
            getQuestions = true // am descarcat id-urile intrebarilor

            totalQuestions = questionIds.size

            // Iterate through questionIds and call getQuestion for each
            questionIds.forEachIndexed { progres, questionId ->
                Log.d(TAG, "Downloading question ${progres + 1}/$totalQuestions")

//                val question = service.getQuestion(questionId)
//                allQuestions.add(question)

                try {
                    val question = service.getQuestion(questionId)
                    progress = progres + 1
                    allQuestions.add(question)
//                    (allQuestions as MutableList<Question>).add(question)
                } catch (e: Exception) {
                    // Se pierde conexiunea
                    Log.e(TAG, "Error downloading question $questionId: ${e.message}")
                    Toast.makeText(context, "No internet connection - Error Downloading", Toast.LENGTH_LONG).show()
                    retryVisibility = 1.0f
                    isClicked = false // oprește ciclul de descărcare
                    return@LaunchedEffect
                }


                if (progres + 1 == totalQuestions) {
                    Log.d(TAG, "All questions downloaded")
                    onClose(allQuestions)
                }
            }
        }
    }

    LaunchedEffect(retry){
        Log.d("RETRY", "RETRY DOWNLOAD")
        questionIds.forEachIndexed { progres, questionId ->
            Log.d(TAG, "Downloading question ${progres + 1}/$totalQuestions")
            try {
                val question = service.getQuestion(questionId)
                progress = progres + 1
                allQuestions.add(question)
            } catch (e: Exception) {
                // Se pierde conexiunea
                Log.e(TAG, "Error downloading question $questionId: ${e.message}")
                Toast.makeText(context, "No internet connection - Error Downloading", Toast.LENGTH_LONG).show()
                return@LaunchedEffect
            }
            if (progres + 1 == totalQuestions) {
                Log.d(TAG, "All questions downloaded")
                onClose(allQuestions)
            }
        }
    }
}