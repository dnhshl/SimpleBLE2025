package com.example.main.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.main.model.MainViewModel

@Composable
fun InfoDialogScreen(viewModel: MainViewModel, navController: NavController) {
    AlertDialog(
        title = {
            Text(text = "Wichtige Info")
        },
        text = {
            Text(text = "Gehen Sie nicht über Los!")
        },
        onDismissRequest = { navController.popBackStack() },
        confirmButton = {
            TextButton(
                onClick = { navController.popBackStack() }
            ) {
                Text("OK")
            }
        }
    )
}