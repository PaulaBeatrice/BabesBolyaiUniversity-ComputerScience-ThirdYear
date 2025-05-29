package com.example.myfirstapp.Teste.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfirstapp.R
import com.example.myfirstapp.Teste.core.TAG
import com.example.myfirstapp.Teste.domain.Question
import kotlinx.coroutines.delay
import androidx.compose.runtime.LaunchedEffect



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionItem(option: Int, isSelected: Boolean, onClick: () -> Unit) {
    val textStyle = if (isSelected) {
        TextStyle(fontWeight = FontWeight.Bold, color = Color.Blue)
    } else {
        TextStyle(fontWeight = FontWeight.Normal, color = Color.Black)
    }

    val backgroundColor = if (isSelected) {
        Color.Yellow // Sau orice altă culoare dorită pentru fundalul selectat
    } else {
        Color.Transparent // Fundal transparent pentru starea ne-selecționată
    }


    Text(
        text = "Option: $option",
        style = textStyle,
        modifier = Modifier
            .clickable { onClick() }
            .background(backgroundColor)
//            .size(18.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(downloadedQuestion: List<Question>) {
    var visibility by remember { mutableStateOf(0.0f) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var correctAnswers by remember { mutableStateOf(0) }
    var givenAnswers by remember { mutableStateOf(0) }
    var indexChoosenOption by remember { mutableStateOf(-1) }
    var autoProceedTimer by remember { mutableStateOf(5) }

//    LaunchedEffect(autoProceedTimer) {
//        delay(1000L)
//        if (autoProceedTimer > 0 && visibility == 0.0f) {
//            autoProceedTimer--
//        } else {
//            if (indexChoosenOption == -1) { // Nu s-a ales nicio opțiune
//                Log.d(TAG, "Auto proceeding to next question...")
//                visibility = 0.0f
//                selectedOption = null
//                autoProceedTimer = 5 // Resetează cronometrul pentru următoarea întrebare
//                currentQuestionIndex++
//            }
//        }
//    }

    LaunchedEffect(autoProceedTimer) {
        Log.d("TIME", "${autoProceedTimer}")
        while (autoProceedTimer > 0 && visibility == 0.0f) {
            delay(1000L)
            autoProceedTimer--

            Log.d("TIMP","${autoProceedTimer}     ${indexChoosenOption}")

            // Adăugăm această verificare suplimentară pentru a evita blocarea
            if (autoProceedTimer == 0 && indexChoosenOption == -1) {
                // Trecem automat la următoarea întrebare dacă timpul a expirat și nu s-a ales nicio opțiune
                Log.d(TAG, "Auto proceeding to next question...")
                visibility = 0.0f
                selectedOption = null
                autoProceedTimer = 5 // Resetează cronometrul pentru următoarea întrebare

                currentQuestionIndex++
            }
        }
        if (indexChoosenOption == -1) { // Nu s-a ales nicio opțiune
            Log.d(TAG, "Auto proceeding to next question...")
            visibility = 0.0f
            selectedOption = null
            autoProceedTimer = 5 // Resetează cronometrul pentru următoarea întrebare
            indexChoosenOption = -1
            currentQuestionIndex++
        }
    }

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = stringResource(id = R.string.main_page)) }) },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(30.dp)
        ) {
            if (downloadedQuestion.isNotEmpty() && currentQuestionIndex < downloadedQuestion.size) {
                val currentQuestion = downloadedQuestion[currentQuestionIndex]

                Text("Question: ${currentQuestionIndex + 1} / ${downloadedQuestion.size}")
                Text("Correct Answers: ${correctAnswers} / ${givenAnswers}")

                // Display the question text
                Text(text = currentQuestion.text)

                // Display labels for each option
                for ((index, option) in currentQuestion.options.withIndex()) {
                    OptionItem(
                        option = option,
                        isSelected = option == selectedOption,
                        onClick = {
                            indexChoosenOption = index
                            Log.d(TAG, "Option clicked: $option  $index    ${currentQuestion.indexCorrectOptions}")
                            visibility = 1.0f
                            selectedOption = option
                        }
                    )
                }

                Button(
                    onClick = {
                        Log.d(TAG, "Next button clicked...")
                        if(indexChoosenOption == currentQuestion.indexCorrectOptions)
                            correctAnswers += 1
                        givenAnswers++
                        currentQuestionIndex++
                        visibility = 0.0f
                        autoProceedTimer = 5
                        indexChoosenOption = -1
                        selectedOption = null
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .alpha(visibility)
                ) {
                    Text("Next")
                }
            } else {
                // Handle the case when there are no questions or all questions have been displayed
                Text(text = "No more questions available")
                Text("Correct Answers: ${correctAnswers} / ${givenAnswers}")
            }
        }
    }
}