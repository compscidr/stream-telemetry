package com.jasonernst.stream_telemetry

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.content.Context
import com.jasonernst.stream_telemetry.UUIDUtils.GAP_NAME_CHARACTERISTIC
import com.jasonernst.stream_telemetry.UUIDUtils.HEART_RATE_CONTROL_DESCRIPTOR
import com.jasonernst.stream_telemetry.UUIDUtils.HEART_RATE_MEASUREMENT_CHARACTERISTIC
import com.jasonernst.stream_telemetry.UUIDUtils.HEART_RATE_SERVICE
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.ByteOrder

@SuppressLint("MissingPermission")
class GattCallback(private val context: Context): BluetoothGattCallback() {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        //logger.debug("BT GATT: $gatt, STATUS: $status, NEWSTATE: $newState")
        when (newState) {
            BluetoothGatt.STATE_CONNECTED -> {
                logger.info("CONNECTED to ${gatt?.device?.name} ${gatt?.device?.address}")
                gatt?.requestMtu(256)
                gatt?.discoverServices()
            }
            BluetoothGatt.STATE_DISCONNECTED -> {
                logger.debug("DISCONNECTED")
                val address = gatt?.device?.address
                if (address != null) {
                    BluetoothViewModel.setConnecting(address, false)
                    if (BluetoothViewModel.getIsHeartbeat(address) == PeerState.UNKNOWN) {
                        BluetoothViewModel.setIsHeartbeat(address, PeerState.FALSE)
                    }
                }
            }
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        val gattServices = gatt?.services
        var hasHeartRate = false
        gattServices?.forEach { gattService ->
            if (gattService.uuid == HEART_RATE_SERVICE) {
                logger.info("GOT HEART RATE SERVICE")
                hasHeartRate = true
                BluetoothViewModel.setIsHeartbeat(gatt.device.address, PeerState.TRUE)
                val gattCharacteristics = gattService.characteristics
                gattCharacteristics.forEach { gattCharacteristic ->
                    if (gattCharacteristic.uuid == HEART_RATE_MEASUREMENT_CHARACTERISTIC) {
                        logger.info("TRYING TO READ HEART RATE CHARACTERISTIC")
                        gatt.setCharacteristicNotification(gattCharacteristic, true)
                        val descriptor =
                            gattCharacteristic.getDescriptor(HEART_RATE_CONTROL_DESCRIPTOR)
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    }
                }
            }
        }
        if (hasHeartRate.not()) {
            gatt?.device?.address?.let { BluetoothViewModel.setIsHeartbeat(it, PeerState.FALSE) }
            gatt?.disconnect()
        }
    }

    override fun onCharacteristicRead(
        gatt: BluetoothGatt,
        characteristic: BluetoothGattCharacteristic,
        value: ByteArray,
        status: Int
    ) {
        val valueAsString = String(value)
        if (characteristic.uuid == GAP_NAME_CHARACTERISTIC) {
            val address = gatt.device.address
            BluetoothViewModel.updateName(address, valueAsString)
            logger.info("GOT NAME $valueAsString")
            gatt.disconnect()
        } else if (characteristic.uuid == HEART_RATE_MEASUREMENT_CHARACTERISTIC) {
            logger.info("GOT HEART RATE: $valueAsString")
            gatt.disconnect()
        } else {
            logger.info("SERVICE UUID: ${characteristic.service.uuid}, CHAR UUID: ${characteristic.uuid}")
        }
    }

    fun toInt16(bytes: ByteArray, index: Int): Short {
        require(bytes.size == 2) { "length must be 2, got: ${bytes.size}" }
        return ByteBuffer.wrap(bytes, index, 2).order(ByteOrder.BIG_ENDIAN).short
    }

    override fun onCharacteristicChanged(
        gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?
    ) {
        if (characteristic?.uuid == HEART_RATE_MEASUREMENT_CHARACTERISTIC) {
            val byteArrayValue = characteristic?.value
            if (byteArrayValue != null) {
                val value = toInt16(byteArrayValue, 0)
                //logger.info("GOT HEART RATE: $value")
                BluetoothViewModel.heartRate.intValue = value.toInt()
            }
        }
    }
}