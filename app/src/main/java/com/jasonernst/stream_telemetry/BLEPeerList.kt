package com.jasonernst.stream_telemetry

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.preference.PreferenceManager
import com.jasonernst.stream_telemetry.BLEScanner.Companion.DEVICE_KEY

@SuppressLint("MissingPermission")
@Composable
fun BLEPeerItem(blePeer: BLEPeer) {
    val context = LocalContext.current
    if (blePeer.isHeartbeat == PeerState.TRUE) {
        Row (modifier = Modifier.clickable {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.edit().putString(DEVICE_KEY, blePeer.bleDevice.getAddress()).commit()
        }) {
            Column() {
                Text("Name: ${blePeer.bleDevice.getName()}")
                Text("Address: ${blePeer.bleDevice.getAddress()}")
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun BLEPeerList() {
    val context = LocalContext.current
    val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    val savedDevice = sharedPreferences.getString(DEVICE_KEY, "")

    if (savedDevice == "") {
        Text("Select a heartbeat sensor device")
    }

    val sortedPeers = BluetoothViewModel.blePeers.values.sortedByDescending { it.firstSeen }
    LazyColumn {
        items(sortedPeers) {
            BLEPeerItem(blePeer = it)
        }
    }
}