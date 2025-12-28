
package com.example.forcegauge.screens

import android.Manifest
import android.bluetooth.BluetoothDevice
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun BluetoothScanScreen(navController: NavController, viewModel: BluetoothViewModel) {
    val discoveredDevices = viewModel.discoveredDevices.collectAsState().value
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.BLUETOOTH_SCAN] == true &&
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                viewModel.startScan()
            }
        }
    )

    Column {
        Button(onClick = {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_CONNECT
            )
            if (permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }) {
                viewModel.startScan()
            } else {
                permissionLauncher.launch(permissions)
            }
        }) {
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
fun DeviceListItem(device: BluetoothDevice, navController: NavController, viewModel: BluetoothViewModel) {
    Text(
        text = "${device.name ?: "Unknown Device"} (${device.address})",
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                viewModel.connectToDevice(device)
                navController.navigate("force_gauge")
            }
            .padding(16.dp)
    )
}
