package com.shubhamthorat.androidtechnicaldeepdive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import com.shubhamthorat.androidtechnicaldeepdive.ui.theme.AndroidTechnicalDeepDiveTheme
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AndroidTechnicalDeepDiveTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Practice Flow Here
                    LaunchedEffect(Unit) {
                        FlowBasics.simpleFlow().collect { value ->
                            println("Flow Practice: Received $value")
                        }
                    }

                    Greeting(
                        name = "Flow Master",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidTechnicalDeepDiveTheme {
        Greeting("Android")
    }
}