package com.jerry.ronaldo.siufascore.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.mutableStateMapOf
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class PermissionManager {
    private val _permissionStates = mutableStateMapOf<PermissionType, PermissionState>()
    val permissionStates: Map<PermissionType, PermissionState>
        get() = _permissionStates
    private val _events = Channel<PermissionEvent>()
    val events: Flow<PermissionEvent>
        get() = _events.receiveAsFlow()
    private var permissionLauncher: ManagedActivityResultLauncher<String, Boolean>? = null
    private var settingsLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>? = null

    fun initPermissionState(context: Context, permissionType: PermissionType) {
        _permissionStates[permissionType] = PermissionState(
            permissionType = permissionType,
            isGranted = permissionType.isGranted(context),
        )
    }

    fun requestPermission(permissionType: PermissionType) {
        _permissionStates[permissionType] = _permissionStates[permissionType]?.copy(
            isLoading = true,
            hasBeenRequested = true
        ) ?: PermissionState(permissionType, isLoading = true, hasBeenRequested = true)
        when (permissionType) {
            PermissionType.Notification -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher?.launch(permissionType.permission)
                } else {
                    // For older versions, directly open settings
                    launchEvent(PermissionEvent.OpenSettings(permissionType))
                }
            }

            else -> {
                permissionLauncher?.launch(permissionType.permission)
            }
        }
    }

    fun handlePermissionResult(permissionType: PermissionType, isGranted: Boolean) {
        _permissionStates[permissionType] = _permissionStates[permissionType]?.copy(
            isGranted = isGranted,
            isLoading = false
        ) ?: PermissionState(permissionType, isGranted = isGranted)

        if (isGranted) {
            launchEvent(PermissionEvent.PermissionGranted(permissionType))
        } else {

            launchEvent(PermissionEvent.PermissionDenied(permissionType))

        }

    }

    fun refreshPermissionState(context: Context, permissionType: PermissionType) {
        val isGranted = permissionType.isGranted(context)
        _permissionStates[permissionType] = _permissionStates[permissionType]?.copy(
            isGranted = isGranted,
            isLoading = false
        ) ?: PermissionState(permissionType, isGranted = isGranted)
    }
    fun setLaunchers(
        permissionLauncher: ManagedActivityResultLauncher<String, Boolean>,
        settingsLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    ) {
        this.permissionLauncher = permissionLauncher
        this.settingsLauncher = settingsLauncher
    }

    fun openSettings(context: Context, permissionType: PermissionType) {
        val intent = permissionType.getSettingsIntent(context)
        settingsLauncher?.launch(intent)
    }

    private fun launchEvent(event: PermissionEvent) {
        kotlin.runCatching {
            _events.trySend(event)
        }
    }

    fun getPermissionState(permissionType: PermissionType): PermissionState? {
        return _permissionStates[permissionType]
    }
}


data class PermissionState(
    val permissionType: PermissionType,
    val isGranted: Boolean = false,
    val isLoading: Boolean = false,
    val shouldShowRationale: Boolean = false,
    val hasBeenRequested: Boolean = false
)

sealed class PermissionEvent {
    data class PermissionGranted(val permissionType: PermissionType) : PermissionEvent()
    data class PermissionDenied(val permissionType: PermissionType) : PermissionEvent()
    data class ShowRationale(val permissionType: PermissionType) : PermissionEvent()
    data class OpenSettings(val permissionType: PermissionType) : PermissionEvent()
}