package com.example.myfirstapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.base.app.Containered
import com.example.myfirstapp.base.app.ItemController
import com.example.myfirstapp.base.core.TAG
import com.example.myfirstapp.base.ui.theme.MyFirstAppTheme
import kotlinx.coroutines.launch

class MainActivity() : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d(TAG, "onCreate")
            MyApp {
                // CHANGE HERE !!!
                com.example.myfirstapp.examples.ex1_messages.app.AppNavHost()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            (application as Containered<*, ItemController<*, *, *, *>>).container.itemRepository.openWsClient()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            (application as Containered<*, ItemController<*, *, *, *>>).container.itemRepository.closeWsClient()
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {
    Log.d("MyFirstApp", "recompose")
    MyFirstAppTheme {
        Surface {
            content()
        }
    }
}

