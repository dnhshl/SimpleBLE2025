package com.example.main.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.main.model.MainViewModel


@Composable
fun MainScreen(
    viewModel: MainViewModel,
    navController: NavController,
) {
    val state by viewModel.state.collectAsState()
    // States für die Switches
    val empfangeDatenState = remember { mutableStateOf(false) }
    val ledState = remember { mutableStateOf(false) }
    val blinkenState = remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Empfange Daten Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Empfange Daten",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = empfangeDatenState.value,
                onCheckedChange = { empfangeDatenState.value = it }
            )
        }

        // LED Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "LED",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = ledState.value,
                onCheckedChange = { ledState.value = it }
            )
        }

        // Blinken Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Blinken",
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = blinkenState.value,
                onCheckedChange = { blinkenState.value = it }
            )
        }

        // Zeile für "Wähle Device" und Bearbeiten-Icon
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Wähle Device")
            IconButton(onClick = { viewModel.startDeviceScan() }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Bearbeiten"
                )
            }
        }

        // Buttons "Verbinde" und "Trenne"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = {}) {
                Text("VERBINDE")
            }
            Button(onClick = { viewModel.stopDeviceScan() }) {
                Text("TRENNE")
            }
        }
    }

}