package com.jasonernst.stream_telemetry

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun displayHeartRate() {
    val heartRate = BluetoothViewModel.heartRate.value
    Text("Heart Rate: $heartRate")
}