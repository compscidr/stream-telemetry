package com.jasonernst.stream_telemetry

import java.util.UUID

object UUIDUtils {
    val GAP_SERVICE_UUID = UUID.fromString("00001800-0000-1000-8000-00805f9b34fb")  //convertFromInteger(0x1800)
    val GAP_NAME_CHARACTERISTIC = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb")

    val HEART_RATE_SERVICE = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    val HEART_RATE_MEASUREMENT_CHARACTERISTIC = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")
    val HEART_RATE_CONTROL_DESCRIPTOR = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")



    // https://medium.com/@shahar_avigezer/bluetooth-low-energy-on-android-22bc7310387a
    private fun convertFromInteger(i: Int): UUID {
        val MSB = 0x0000000000001000L
        val LSB = -0x7fffff7fa064cb05L
        val value = (i and -0x1).toLong()
        return UUID(MSB or (value shl 32), LSB)
    }
}