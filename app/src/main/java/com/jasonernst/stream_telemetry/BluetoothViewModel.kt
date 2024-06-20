package com.jasonernst.stream_telemetry

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import org.slf4j.LoggerFactory

object BluetoothViewModel: ViewModel() {
    private val logger = LoggerFactory.getLogger(javaClass)
    val haveLocationPermissions = mutableStateOf(false)
    val haveBtPermissions = mutableStateOf(false)
    val scanningError = mutableStateOf<String?>(null)
    val heartRate = mutableIntStateOf(0)

    // map of mac address -> blepeer
    val blePeers = mutableStateMapOf<String, BLEPeer>()

    fun addOrUpdateBLEPeer(blePeer: BLEPeer) {
        val address = blePeer.bleDevice.getAddress()
        if (address == null) {
            logger.error("BLEPeer has null address: {}", blePeer)
            return
        }
        if (blePeers.containsKey(address)) {
            blePeers[address]?.updateLastSeen(blePeer.rssi)
        } else {
            blePeers[address] = blePeer
        }
    }

    fun updateName(address: String, name: String) {
        blePeers[address]?.bleDevice?.gapName = name
    }

    fun isConnecting(address: String): Boolean {
        return blePeers[address]?.isConnecting ?: false
    }

    fun setConnecting(address: String, connecting: Boolean) {
        blePeers[address]?.isConnecting = connecting
    }

    fun setIsHeartbeat(address: String, heartbeat: PeerState) {
        blePeers[address]?.isHeartbeat = heartbeat
    }

    fun getIsHeartbeat(address: String): PeerState {
        return blePeers[address]?.isHeartbeat ?: PeerState.UNKNOWN
    }
}