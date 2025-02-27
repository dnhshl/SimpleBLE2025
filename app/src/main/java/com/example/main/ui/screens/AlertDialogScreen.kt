package com.example.main.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.main.model.MainViewModel

@Composable
fun AlertDialogScreen(
    viewModel: MainViewModel,
    navController: NavController
) {

    AlertDialog(
        title = { Text("Verbindung trennen?") },
        text = { Text("Wollen Sie die Verbindung zum Gerät trennen?") },
        onDismissRequest = {
            navController.popBackStack()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.disconnect()
                    navController.popBackStack()
                }) {
                Text("Ja")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    navController.popBackStack()
                }) {
                Text("Nein")
            }
        }
    )
}