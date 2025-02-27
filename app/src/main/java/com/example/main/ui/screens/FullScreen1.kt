package com.example.main.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.main.R
import com.example.main.model.ConnectionState
import com.example.main.model.MainViewModel
import com.juul.kable.Advertisement


@Composable
fun FullScreen1(
    viewModel: MainViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsState()
    val selectedAdvertisement = state.selectedAdvertisement
    val connectionState = state.connectionState
    val receiveData = state.receiveData
    val led = state.led
    val blink = state.blink
    val esp32DataIn = state.esp32DataIn


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val isEnabled = connectionState == ConnectionState.CONNECTED

        // Empfange Daten Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (isEnabled) 1f else 0.5f)
                .clickable(enabled = isEnabled) {},
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Empfange Daten",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = receiveData,
                onCheckedChange = { viewModel.setReceiveData(it) },
                enabled = isEnabled
            )
        }

        // LED Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (isEnabled) 1f else 0.5f)
                .clickable(enabled = isEnabled) {},
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LED",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = led,
                onCheckedChange = { viewModel.setLed(it) },
                enabled = isEnabled
            )
        }

        // Blinken Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (isEnabled) 1f else 0.5f)
                .clickable(enabled = isEnabled) {},
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Blinken",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = blink,
                onCheckedChange = { viewModel.setBlink(it) },
                enabled = isEnabled
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ConnectionStatusCard(
                advertisement = selectedAdvertisement,
                connectionState = connectionState,
                viewModel = viewModel,
                navController = navController
            )
        }

        esp32DataIn?.let {
            Text(
                text = "${it.potiArray.toString()}\n${it.ledstatus}",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }

    }

}

@Composable
fun ConnectionStatusCard(
    advertisement: Advertisement?,
    connectionState: Int = ConnectionState.NO_DEVICE,
    viewModel: MainViewModel,
    navController: NavController
) {
    // Menu State
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {

        val color = when (connectionState) {
            ConnectionState.NO_DEVICE -> Color.Transparent
            ConnectionState.NOT_CONNECTED -> Color(0xFFE57373)
            ConnectionState.CONNECTING -> Color.Yellow
            ConnectionState.CONNECTED -> Color.Green
            else -> Color.Transparent
        }

        val statusMessage = when (connectionState) {
            ConnectionState.NO_DEVICE -> ""
            ConnectionState.NOT_CONNECTED -> "nicht verbunden"
            ConnectionState.CONNECTING -> "Connecting ..."
            ConnectionState.CONNECTED -> "Verbunden"
            else -> ""
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = advertisement?.name?.toString() ?: "kein Device ausgewählt",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = advertisement?.identifier?.toString() ?: "",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier=Modifier.height(4.dp))
                    Text(text = statusMessage, style = MaterialTheme.typography.bodyLarge)
                }

                // Dropdown Menu Button
                Box { // To position the DropdownMenu correctly
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "More Options")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.menuItem1)) },
                            onClick = {
                                navController.navigate(MyScreens.FullScreen2.route)
                                expanded = false
                            },
                            enabled = connectionState == ConnectionState.NO_DEVICE
                                    || connectionState == ConnectionState.NOT_CONNECTED
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.menuItem2)) },
                            onClick = {
                                viewModel.connect(advertisement = advertisement)
                                expanded = false
                            },
                            enabled = connectionState == ConnectionState.NOT_CONNECTED
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(id = R.string.menuItem3)) },
                            onClick = {
                                navController.navigate(MyScreens.AlertDialog.route)
                                expanded = false
                            },
                            enabled = connectionState == ConnectionState.CONNECTED
                        )


                    }
                }

            }


        }
    }

}