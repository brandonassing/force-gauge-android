
package com.example.forcegauge.screens

import android.annotation.SuppressLint
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@SuppressLint("MissingPermission") // Permissions are handled in the UI layer
class BluetoothViewModel(application: Application) : AndroidViewModel(application) {

    private val bluetoothManager: BluetoothManager = application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager.adapter
    private val bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
    private var bluetoothGatt: BluetoothGatt? = null

    private val _discoveredDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<BluetoothDevice>> = _discoveredDevices

    private val _rawForce = MutableStateFlow(0f)
    private val _currentForce = MutableStateFlow(0f)
    val currentForce: StateFlow<Float> = _currentForce

    private val _maxForce = MutableStateFlow(0f)
    val maxForce: StateFlow<Float> = _maxForce

    private var tareOffset = 0f

    // Replace with your device's actual service and characteristic UUIDs
    private val forceServiceUuid: UUID = UUID.fromString("38e5fea4-d56b-4178-9a80-015dc896a0d1") // Weight Scale Service
    private val forceCharacteristicUuid: UUID = UUID.fromString("55d0591b-4bdd-4207-81f4-d7d1753de97f") // Weight Measurement

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            if (result.device.name != null && !_discoveredDevices.value.any { it.address == result.device.address }) {
                _discoveredDevices.value = _discoveredDevices.value + result.device
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothGatt.STATE_CONNECTED) {
                gatt.discoverServices()
            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                // Handle disconnection
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val service = gatt.getService(forceServiceUuid)
            val characteristic = service?.getCharacteristic(forceCharacteristicUuid)
            if (characteristic != null) {
                gatt.setCharacteristicNotification(characteristic, true)
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            // Assuming the characteristic value is a float
            val newRawForce = characteristic.value.toString().toFloatOrNull() ?: 0f
            _rawForce.value = newRawForce

            val adjustedForce = newRawForce - tareOffset
            _currentForce.value = adjustedForce

            if (adjustedForce > _maxForce.value) {
                _maxForce.value = adjustedForce
            }
        }
    }

    fun startScan() {
        _discoveredDevices.value = emptyList()
        bluetoothLeScanner?.startScan(scanCallback)
    }

    fun connectToDevice(device: BluetoothDevice) {
        bluetoothLeScanner?.stopScan(scanCallback)
        bluetoothGatt = device.connectGatt(getApplication(), false, gattCallback)
    }

    fun resetAndTare() {
        tareOffset = _rawForce.value
        _maxForce.value = 0f
    }

    override fun onCleared() {
        super.onCleared()
        bluetoothGatt?.close()
    }
}
