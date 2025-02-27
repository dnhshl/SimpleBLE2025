package com.example.main.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import com.example.main.model.MainViewModel
import com.example.main.ui.screens.AlertDialogScreen
import com.example.main.ui.screens.FullScreen1
import com.example.main.ui.screens.FullScreen2
import com.example.main.ui.screens.MyScreens

@Composable
fun MyNavHost(
    navController: NavHostController,
    viewModel: MainViewModel,
    startDestination: String,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Screens via BottomBar und Fullscreens

        composable(MyScreens.FullScreen1.route) { FullScreen1(viewModel, navController) }
        composable(MyScreens.FullScreen2.route) { FullScreen2(viewModel, navController) }

        // Dialog Screens
        dialog(MyScreens.AlertDialog.route) { AlertDialogScreen(viewModel, navController) }

    }
}
