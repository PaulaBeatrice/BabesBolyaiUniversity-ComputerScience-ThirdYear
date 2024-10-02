package com.example.myfirstapp.auth

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myfirstapp.R

val TAG = "LoginScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onClose: () -> Unit) {
    val loginViewModel = viewModel<LoginViewModel>(factory = LoginViewModel.Factory)
    val loginUiState = loginViewModel.uiState

    val defaultFieldModifier = Modifier.fillMaxWidth().padding(10.dp)
        .border(0.dp, MaterialTheme.colorScheme.secondary, RectangleShape)

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text(text = stringResource(id = R.string.login)) }) },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
                .padding(30.dp)
        ) {
            var username by remember { mutableStateOf("") }
            TextField(
                label = { Text(text = "Username") },
                value = username,
                onValueChange = { username = it },
                modifier = defaultFieldModifier
            )
            var password by remember { mutableStateOf("") }
            TextField(
                label = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation(),
                value = password,
                onValueChange = { password = it },
                modifier = defaultFieldModifier
            )
            Log.d(TAG, "recompose")

            var shakeOffset by remember { mutableStateOf(0.dp) }

            Button(
                onClick = {
                    Log.d(TAG, "login...")
                    loginViewModel.login(username, password)
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            ) {
                LaunchedEffect(Unit) {
                    while (true) {
                        withFrameNanos {
//                            shakeOffset = (Math.sin(it / 1e6) * 10).toFloat().dp
                            shakeOffset = (Math.sin(it / 1e6) * 10 + Math.sin(it / 1e5) * 5).toFloat().dp

                        }
                    }
                }

                // Adaugăm o animație de shake la textul butonului
                val translationX by animateFloatAsState(
                    targetValue = if (shakeOffset > 0.dp) 0f else with(LocalDensity.current) { shakeOffset.toPx() },
                    animationSpec = tween(durationMillis = 100)
                )

                Text(
                    "Login",
                    modifier = Modifier
                        .offset(x = translationX)
                )
            }

            if (loginUiState.isAuthenticating) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(15.dp)
                )
            }
            if (loginUiState.authenticationError != null) {
                Text(text = "Login failed ${loginUiState.authenticationError.message}")
            }
        }
    }

    LaunchedEffect(loginUiState.authenticationCompleted) {
        Log.d(TAG, "Auth completed")
        if (loginUiState.authenticationCompleted) {
            onClose()
        }
    }
}

@Composable
private fun Modifier.Companion.offset(x: Float): Modifier {
    return Modifier.graphicsLayer(translationX = x)
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen({})
}
