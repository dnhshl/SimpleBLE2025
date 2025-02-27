package com.example.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.main.ui.screens.MyApp
import com.example.main.ui.theme.MultiScreenNavTemplateTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiScreenNavTemplateTheme {
                MyApp()
            }
        }
    }
}
