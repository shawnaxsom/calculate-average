package com.shawnaxsom.average

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.shawnaxsom.average.ui.AverageScreen
import com.shawnaxsom.average.ui.theme.AverageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            AverageTheme {
                AverageScreen()
            }
        }
    }
}
