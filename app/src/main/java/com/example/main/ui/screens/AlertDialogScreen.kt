package com.example.main.ui.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.example.main.R
import com.example.main.model.MainViewModel

@Composable
fun AlertDialogScreen(
    viewModel: MainViewModel,
    navController: NavController
) {

    AlertDialog(
        title = { Text(stringResource(R.string.alertTitle)) },
        text = { Text(stringResource(R.string.alertText)) },
        onDismissRequest = {
            navController.popBackStack()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.disconnect()
                    navController.popBackStack()
                }) {
                Text(stringResource(R.string.alertConfirm))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    navController.popBackStack()
                }) {
                Text(stringResource(R.string.alertDismiss))
            }
        }
    )
}