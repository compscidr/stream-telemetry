package com.jasonernst.stream_telemetry

import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import org.slf4j.LoggerFactory

class MainActivity: ComponentActivity() {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TelemetryApp()
        }
    }
}

@Composable
fun TelemetryApp() {
    Column {
        ShowPermissionRequestButtons()
        ShowErrors()
        BLEPeerList()
        displayHeartRate()
    }
}