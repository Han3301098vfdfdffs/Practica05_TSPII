package com.example.practica04_tsp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.practica04_tsp.ui.screens.home.HomeScreen
import com.example.practica04_tsp.ui.theme.Practica04_TSPTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Practica04_TSPTheme {
                HomeScreen()
            }
        }
    }
}
