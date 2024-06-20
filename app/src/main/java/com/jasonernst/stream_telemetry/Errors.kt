package com.jasonernst.stream_telemetry

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jasonernst.stream_telemetry.theme.NoticeBackgroundColor

@Composable
fun ShowError(error: String) {
    Row(
        modifier =
        Modifier
            .background(color = NoticeBackgroundColor)
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(error)
    }
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun ShowErrors(isPreview: Boolean = false) {
    Column(
    ) {
        val scanningError = BluetoothViewModel.scanningError.value
        if (scanningError != null) {
            ShowError("Scanning Error: $scanningError")
        }
    }
}