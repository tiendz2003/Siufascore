package com.jerry.ronaldo.siufascore.presentation.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.jerry.ronaldo.siufascore.utils.PermissionManager

@Composable
fun rememberPermissionManager(): PermissionManager {
    val context = LocalContext.current
    val permissionManager = remember { PermissionManager() }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        // We need to track which permission was requested
        // This is handled through the PermissionComponent
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        // Refresh all permission states when returning from settings
        permissionManager.permissionStates.keys.forEach { permissionType ->
            permissionManager.refreshPermissionState(context, permissionType)
        }
    }

    LaunchedEffect(permissionLauncher, settingsLauncher) {
        permissionManager.setLaunchers(permissionLauncher, settingsLauncher)
    }

    return permissionManager
}