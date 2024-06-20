package com.jasonernst.stream_telemetry

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.os.Build
import android.os.ParcelUuid
import androidx.preference.PreferenceManager
import com.jasonernst.stream_telemetry.BLEScanner.Companion.DEVICE_KEY
import com.jasonernst.stream_telemetry.UUIDUtils.HEART_RATE_SERVICE
import org.slf4j.LoggerFactory

class BLEScanCallback(private val context: Context): ScanCallback() {
    private val logger = LoggerFactory.getLogger(javaClass)

    @SuppressLint("MissingPermission")
    override fun onScanResult(callbackType: Int, result: ScanResult) {
        super.onScanResult(callbackType, result)

        val primaryPhy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result.primaryPhy
        } else {
            -1
        }
        val secondaryPhy = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            result.secondaryPhy
        } else {
            -1
        }

        val blePeer = BLEPeer(BluetoothDeviceWrapper(result.device), result.rssi, primaryPhy, secondaryPhy)
        BluetoothViewModel.addOrUpdateBLEPeer(blePeer)

        if (result.scanRecord?.serviceUuids?.contains(ParcelUuid(HEART_RATE_SERVICE)) == true) {
            BluetoothViewModel.setIsHeartbeat(result.device.address, PeerState.TRUE)
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val savedDevice = sharedPreferences.getString(DEVICE_KEY, "")

        if (savedDevice != "") {
            if (result.device.address == savedDevice) {
                if (BluetoothViewModel.isConnecting(result.device.address).not()) {
                    BluetoothViewModel.setConnecting(result.device.address, true)
                    val gattCallback = GattCallback(context)
                    result.device.connectGatt(context, true, gattCallback)
                }
            }
        }
    }
}