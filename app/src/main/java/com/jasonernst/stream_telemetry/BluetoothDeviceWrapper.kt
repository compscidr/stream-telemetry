package com.jasonernst.stream_telemetry

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice

/**
 * Wrapper class for BluetoothDevice so we can do previews (we can't mock the class directly because
 * its not open)
 */
@SuppressLint("MissingPermission")
class BluetoothDeviceWrapper(val device: BluetoothDevice?, val preview: Boolean = false, var gapName: String? = null) {

    fun getName(): String? {
        return if (preview) {
            "Preview Device"
        } else {
            if (device?.name == null) {
                gapName
            } else {
                device.name
            }
        }
    }

    fun getAddress(): String? {
        return if (preview) {
            "00:00:00:00:00:00"
        } else {
            device?.address
        }
    }

    fun getType(): Int {
        return if (preview) {
            0
        } else {
            device?.type ?: 0
        }
    }

    fun getBondState(): Int {
        return if (preview) {
            0
        } else {
            device?.bondState ?: 0
        }
    }

    fun getBluetoothClass(): Int {
        return if (preview) {
            0
        } else {
            device?.bluetoothClass?.deviceClass ?: 0
        }
    }

    fun getUUIDs(): Array<android.os.ParcelUuid>? {
        return if (preview) {
            null
        } else {
            device?.uuids
        }
    }
}