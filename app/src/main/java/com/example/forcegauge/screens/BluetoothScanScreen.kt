
package com.example.forcegauge.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BluetoothScanScreen(navController: NavController, viewModel: BluetoothViewModel) {
    val discoveredDevices = viewModel.discoveredDevices.collectAsState().value

    Column {
        Button(onClick = { viewModel.startScan() }) {
            Text("Start Scanning")
        }
        LazyColumn {
            items(discoveredDevices) { device ->
                DeviceListItem(device = device, navController = navController, viewModel = viewModel)
            }
        }
    }
}

@Composable
fun DeviceListItem(device: FakeBluetoothDevice, navController: NavController, viewModel: BluetoothViewModel) {
    Text(
        text = "${device.name} (${device.address})",
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.connectToDevice(device)
                navController.navigate("force_gauge")
            }
            .padding(16.dp)
    )
}
