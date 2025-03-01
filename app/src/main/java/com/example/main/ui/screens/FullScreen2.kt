package com.example.main.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.main.R
import com.example.main.model.MainViewModel


@Composable
fun FullScreen2(
    viewModel: MainViewModel,
    navController: NavController,
) {

    val state by viewModel.state.collectAsState()
    val devices = state.devices
    val selectedDevice = state.selectedDevice

    LaunchedEffect(Unit) {
        viewModel.startCollectDevices()
        kotlinx.coroutines.delay(5000)
        viewModel.stopCollectDevices()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        if (devices.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(devices) { device ->
                    ListItemCard(
                        title = device.title,
                        subtitle = device.subtitle,
                        isSelected = (selectedDevice?.id == device.id),
                        onItemClick = {
                            viewModel.setSelectedDevice(device)
                            navController.popBackStack()
                        },
                    )
                }
            }
        } else {
            Text(stringResource(R.string.noDevices), fontSize = 24.sp)
        }
    }

}

@Composable
fun ListItemCard(
    title: String,
    subtitle: String,
    isSelected: Boolean = false,
    onItemClick: () -> Unit,
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(onClick = onItemClick),
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onItemClick() }
                .background(if (isSelected) MaterialTheme.colorScheme.tertiaryContainer else Color.Transparent)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
            }
        }
    }

}