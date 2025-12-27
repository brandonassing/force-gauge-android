
package com.example.forcegauge.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.forcegauge.screens.BluetoothViewModel

@Composable
fun ForceGaugeScreen(viewModel: BluetoothViewModel) {
    val currentForce = viewModel.currentForce.collectAsState().value
    val maxForce = viewModel.maxForce.collectAsState().value

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Current Force: $currentForce lbs")
        Text(text = "Max Force: $maxForce lbs")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.resetAndTare() }) {
            Text(text = "Reset")
        }
    }
}
