package com.example.main.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.model.MainViewModel

@Composable
fun Screen3(viewModel: MainViewModel, navController: NavController) {

    val pstate by viewModel.pState.collectAsState()
    val name = pstate.name



    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Hallo Screen 3", fontSize = 24.sp)
        Spacer(modifier = Modifier.size(16.dp))
        TextField(
            value = name,
            label = { Text("Name") },
            onValueChange = { },
            singleLine = true
        )

    }
}