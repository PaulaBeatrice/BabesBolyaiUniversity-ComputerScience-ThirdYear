package com.example.myfirstapp.orders1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.example.myfirstapp.orders1.core.TAG
import com.example.myfirstapp.ui.theme.MyFirstAppTheme
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.d(TAG, "onCreate")

            MyApp {
                MyAppNavHost()
            }
        }
    }



    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            (application as MyFirstApplication).container.itemRepository.openWsClient()
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            (application as MyFirstApplication).container.itemRepository.closeWsClient()
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
