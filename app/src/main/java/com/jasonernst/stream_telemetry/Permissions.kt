package com.jasonernst.stream_telemetry

import android.Manifest
import android.os.Build
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ShowPermissionRequestButtons(isPreview: Boolean = false, showPermissions: Boolean = true) {
    val showLocationRequest: Boolean
    val showBluetoothRequest: Boolean
    var locationAction = {}
    var bluetoothAction = {}
    if (isPreview) {
        showLocationRequest = showPermissions
        showBluetoothRequest = showPermissions
    } else {
        val locationPermissionsState = rememberMultiplePermissionsState(
            listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
        )
        showLocationRequest = locationPermissionsState.allPermissionsGranted.not()
        locationAction = { locationPermissionsState.launchMultiplePermissionRequest() }
        val bluetoothPermissionsState = rememberMultiplePermissionsState(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                listOf(
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH,
                )
            } else {
                listOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.BLUETOOTH,
                )
            }
        )
        showBluetoothRequest = bluetoothPermissionsState.allPermissionsGranted.not()
        bluetoothAction = { bluetoothPermissionsState.launchMultiplePermissionRequest() }
    }
    if (showLocationRequest) {
        Text("Need Location Permissions")
        Button(onClick = locationAction) {
            Text("Request Location permission")
        }
    } else {
        BluetoothViewModel.haveLocationPermissions.value = true
    }

    if (showBluetoothRequest) {
        Text("Need BT Permissions")
        Button(onClick = bluetoothAction) {
            Text("Request BT permission")
        }
    } else {
        BluetoothViewModel.haveBtPermissions.value = true
    }

    if (BluetoothViewModel.haveBtPermissions.value && BluetoothViewModel.haveBtPermissions.value) {
        val context = LocalContext.current
        val bleScanner = BLEScanner(context)
        bleScanner.startScan()
    }
}