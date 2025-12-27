
package com.example.forcegauge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.forcegauge.screens.BluetoothScanScreen
import com.example.forcegauge.screens.BluetoothViewModel
import com.example.forcegauge.screens.ForceGaugeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val viewModel: BluetoothViewModel = viewModel()

            NavHost(navController = navController, startDestination = "bluetooth_scan") {
                composable("bluetooth_scan") {
                    BluetoothScanScreen(navController = navController, viewModel = viewModel)
                }
                composable("force_gauge") {
                    ForceGaugeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
