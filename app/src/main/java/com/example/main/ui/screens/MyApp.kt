package com.example.main.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.main.R
import com.example.main.model.MainViewModel
import com.example.main.ui.navigation.MyMenu
import com.example.main.ui.navigation.MyNavBar
import com.example.main.ui.navigation.MyNavHost
import com.example.main.ui.navigation.MyTopBar
import com.example.main.ui.screens.MyScreens.Companion.bottomBarScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState


// Show the menu icon in the top bar
const val SHOW_MENU: Boolean = false

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyApp() {

    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()

    // Welche Rechte werden benötigt?
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            android.Manifest.permission.BLUETOOTH_SCAN,
            android.Manifest.permission.BLUETOOTH_CONNECT,
            android.Manifest.permission.BLUETOOTH_ADVERTISE
        )
    } else {
        listOf(
            android.Manifest.permission.BLUETOOTH,
            android.Manifest.permission.BLUETOOTH_ADMIN,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // Abfragen mehrerer Berechtigungen
    val multiplePermissionsState = rememberMultiplePermissionsState(permissions = permissions)

    // Schalte Bildschirminhalt abhängig vom Rechtestatus
    when {
        // wenn Rechte erteilt sind:
        // Location Updates starten und App anzeigen
        // Der komplette Scaffold Inhalt wird in MyAppScaffold definiert
        multiplePermissionsState.allPermissionsGranted -> {
            MyAppScreen(viewModel, navController)
        }
        // Wenn der Nutzer die Rechte schon einmal verweigert hat,
        // aber noch nicht permanent, dann kann hier eine Erklärung
        // angezeigt werden, warum die Rechte benötigt werden
        multiplePermissionsState.shouldShowRationale -> {
            PermissonRationalScreen(multiplePermissionsState)

        }
        // Wenn der Nutzer die Rechte noch nicht erteilt hat, dann
        // kann hier die Anfrage gestartet werden
        // Es wird ein Systemdialog geöffnet, in dem der Nutzer die Rechte
        // erteilen kann
        else -> {
            RequestPermissionScreen(multiplePermissionsState)
        }
    }

}

@Composable
fun MyAppScreen(viewModel: MainViewModel, navController: NavHostController) {


    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    var showMenu by remember { mutableStateOf(false) }


    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        snackbarHost = { SnackbarHost(hostState = viewModel.snackbarHostState) },
        topBar = {
            MyTopBar(
                navController = navController,
                onMenuClick = { showMenu = !showMenu },
            )
        },
        bottomBar = {
            if (bottomBarScreens.any { it.route == currentRoute }) {
                MyNavBar(
                    navController = navController,
                    screens = MyScreens.bottomBarScreens
                )
            }
        }
    ) { paddingValues ->
        MyNavHost(
            navController = navController,
            viewModel = viewModel,
            startDestination = MyScreens.startDestination,
            modifier = Modifier.padding(paddingValues)
        )

        MyMenu(
            showMenu = showMenu,
            navController = navController,
            paddingValues = paddingValues,
            onToggleMenu = { showMenu = !showMenu }
        )

    }


}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissonRationalScreen(permissionState: MultiplePermissionsState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.permissionRational),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        // First-time request or denied permanently, trigger the request
        Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
            Text(stringResource(R.string.requestPermission))
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun RequestPermissionScreen(permissionState: MultiplePermissionsState) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.permissionRequired),
            fontSize = 24.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
        // First-time request or denied permanently, trigger the request
        Button(onClick = { permissionState.launchMultiplePermissionRequest() }) {
            Text(stringResource(R.string.requestPermission))
        }
    }
}