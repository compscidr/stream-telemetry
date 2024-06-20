package com.jasonernst.stream_telemetry

import android.annotation.SuppressLint
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanSettings
import android.content.Context
import androidx.preference.PreferenceManager
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicBoolean


class BLEScanner(private val context: Context) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val isRunning = AtomicBoolean(false)
    private var bleScanCallback: BLEScanCallback? = null

    companion object {
        val DEVICE_KEY = "SAVED_SENSOR"
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val savedDevice = sharedPreferences.getString(DEVICE_KEY, "")

        logger.debug("Starting scan")
        if (isRunning.getAndSet(true)) {
            logger.error("Trying to start but already scanning")
            return
        }
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner
        if (bluetoothLeScanner == null) {
            val error = "Cannot start scanning because BT power is off"
            logger.error(error)
            BluetoothViewModel.scanningError.value = error
            return
        }
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_BALANCED)
            .build()
        val scanFilter = if (savedDevice != "") {
            ScanFilter.Builder()
                .setDeviceAddress(savedDevice)
                .build()
        } else {
            ScanFilter.Builder()
                .build()
        }
        bleScanCallback = BLEScanCallback(context)
        bluetoothLeScanner.startScan(listOf(scanFilter), scanSettings, bleScanCallback)
        isRunning.set(true)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as android.bluetooth.BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        val bluetoothLeScanner = bluetoothAdapter.bluetoothLeScanner

        bluetoothLeScanner.stopScan(bleScanCallback)
    }
}