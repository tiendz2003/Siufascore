package com.jerry.ronaldo.siufascore.designsystem.component

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jerry.ronaldo.siufascore.utils.PermissionEvent
import com.jerry.ronaldo.siufascore.utils.PermissionManager
import com.jerry.ronaldo.siufascore.utils.PermissionType

@Composable
fun PermissionToggleCard(
    permissionType: PermissionType,
    permissionManager: PermissionManager,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier,
    onPermissionChanged: ((Boolean) -> Unit)? = null
) {
    val context = LocalContext.current
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var currentPermissionType by remember { mutableStateOf<PermissionType?>(null) }

    LaunchedEffect (permissionType) {
        permissionManager.initPermissionState(context, permissionType)
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        currentPermissionType?.let { type ->
            permissionManager.handlePermissionResult(type, isGranted)
            onPermissionChanged?.invoke(isGranted)
            if (!isGranted) {
                showRationaleDialog = true
            }
        }
    }

    val settingsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { _ ->
        permissionManager.refreshPermissionState(context, permissionType)
        val newState = permissionManager.getPermissionState(permissionType)
        onPermissionChanged?.invoke(newState?.isGranted ?: false)
    }
    //la'ng nghe su kien
    LaunchedEffect(permissionManager.events) {
        permissionManager.events.collect { event ->
            when (event) {
                is PermissionEvent.ShowRationale -> {
                    if (event.permissionType == permissionType) {
                        showRationaleDialog = true
                    }
                }
                is PermissionEvent.OpenSettings -> {
                    if (event.permissionType == permissionType) {
                        showSettingsDialog = true
                    }
                }
                else -> {  }
            }
        }
    }

    val permissionState = permissionManager.getPermissionState(permissionType)
    val isGranted = permissionState?.isGranted ?: false
    val isLoading = permissionState?.isLoading ?: false

    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal =  16.dp , vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = permissionType.title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = permissionType.title,
                    color = Color.Black,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = if (isGranted) "Đã cấp quyền" else "Chưa cấp quyền",
                    color = if (isGranted) Color(0xFF4CAF50) else Color.Gray,
                    fontSize = 12.sp
                )
            }

            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = iconColor
                )
            } else {
                Switch(
                    checked = isGranted,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            currentPermissionType = permissionType
                            when (permissionType) {
                                is PermissionType.Notification -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(permissionType.permission)
                                    } else {
                                        showSettingsDialog = true
                                    }
                                }
                            }
                        } else {
                            showSettingsDialog = true
                        }
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = iconColor,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFFCCCCCC)
                    )
                )
            }
        }
    }
    if (showRationaleDialog) {
        PermissionRationaleDialog(
            permissionType = permissionType,
            onDismiss = { showRationaleDialog = false },
            onGoToSettings = {
                showRationaleDialog = false
                val intent = permissionType.getSettingsIntent(context)
                settingsLauncher.launch(intent)
            }
        )
    }
    if (showSettingsDialog) {
        PermissionSettingsDialog(
            permissionType = permissionType,
            onDismiss = { showSettingsDialog = false },
            onGoToSettings = {
                showSettingsDialog = false
                val intent = permissionType.getSettingsIntent(context)
                settingsLauncher.launch(intent)
            }
        )
    }
}
@Composable
fun PermissionSettingsDialog(
    permissionType: PermissionType,
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color(0xFF7B68EE),
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Cài đặt ${permissionType.title}",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp
                )
            )
        },
        text = {
            Text(
                text = "Để thay đổi cài đặt ${permissionType.title.lowercase()}, vui lòng vào Cài đặt hệ thống.",
                fontSize = 14.sp,
                style = MaterialTheme.typography.titleLarge,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onGoToSettings,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF7B68EE)
                )
            ) {
                Text("Mở Cài đặt",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Hủy",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp
                    ),)
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun PermissionRationaleDialog(
    permissionType: PermissionType,
    onDismiss: () -> Unit,
    onGoToSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = when (permissionType) {
                    is PermissionType.Notification -> Icons.Default.Notifications
                },
                contentDescription = permissionType.title,
                tint = Color(0xFF7B68EE),
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = "Cần quyền ${permissionType.title}",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = permissionType.rationaleMessage,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        },
        confirmButton = {
            TextButton(
                onClick = onGoToSettings,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color(0xFF7B68EE)
                )
            ) {
                Text("Đi tới Cài đặt")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Hủy")
            }
        },
        containerColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}
