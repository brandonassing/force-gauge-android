
package com.example.forcegauge.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

data class FakeBluetoothDevice(val name: String, val address: String)

class BluetoothViewModel : ViewModel() {

    private val _discoveredDevices = MutableStateFlow<List<FakeBluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<FakeBluetoothDevice>> = _discoveredDevices

    private val _rawForce = MutableStateFlow(0f)
    private val _currentForce = MutableStateFlow(0f)
    val currentForce: StateFlow<Float> = _currentForce

    private val _maxForce = MutableStateFlow(0f)
    val maxForce: StateFlow<Float> = _maxForce

    private var tareOffset = 0f

    fun startScan() {
        _discoveredDevices.value = listOf(
            FakeBluetoothDevice("Fake Device 1", "00:11:22:33:44:55"),
            FakeBluetoothDevice("Fake Device 2", "66:77:88:99:AA:BB"),
            FakeBluetoothDevice("Fake Device 3", "CC:DD:EE:FF:00:11")
        )
    }

    fun connectToDevice(device: FakeBluetoothDevice) {
        // Reset state when connecting to a new device
        tareOffset = 0f
        _rawForce.value = 0f
        _currentForce.value = 0f
        _maxForce.value = 0f
        // Simulate receiving data
        viewModelScope.launch {
            while(true) {
                val newRawForce = Random.nextFloat() * 100
                _rawForce.value = newRawForce

                val adjustedForce = newRawForce - tareOffset
                _currentForce.value = adjustedForce

                if (adjustedForce > _maxForce.value) {
                    _maxForce.value = adjustedForce
                }
                delay(500)
            }
        }
    }

    fun resetAndTare() {
        tareOffset = _rawForce.value
        _maxForce.value = 0f
    }
}
