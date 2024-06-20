package com.jasonernst.stream_telemetry

enum class PeerState {
    TRUE,
    FALSE,
    UNKNOWN
}

data class BLEPeer(
    var bleDevice: BluetoothDeviceWrapper,
    var rssi: Int, var primaryPhy: Int,
    var secondaryPhy: Int,
    var firstSeen: Long = System.currentTimeMillis(),
    var lastSeen: Long = System.currentTimeMillis(),
    var isConnecting: Boolean = false,
    var isHeartbeat: PeerState = PeerState.UNKNOWN) {
    fun updateLastSeen(rssi: Int) {
        this.rssi = rssi
        lastSeen = System.currentTimeMillis()
    }
}