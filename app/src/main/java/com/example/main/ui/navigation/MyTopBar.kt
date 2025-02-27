package com.example.main.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.main.ui.screens.MyScreens
import com.example.main.ui.screens.SHOW_MENU


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopBar(
    navController: NavController,
    onMenuClick: () -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentRoute = currentDestination?.route ?: ""

    val currentScreen = MyScreens.fromRoute(currentRoute)
    var screenTitle = ""
    currentScreen?.let { screenTitle = stringResource(id = it.titleID) }


    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),
        title = {
            Text(
                screenTitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            if (SHOW_MENU) {
                IconButton(onClick = { onMenuClick() }) {
                    Icon(
                        imageVector = Icons.Filled.Menu,
                        contentDescription = "Localized description"
                    )
                }
            }
        },
        navigationIcon = {
            if (currentScreen?.showBackArrow ?: false) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                }
            }
        }
    )
}