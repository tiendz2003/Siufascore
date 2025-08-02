package com.jerry.ronaldo.siufascore.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

sealed class PermissionType(
    val permission: String,
    val title: String,
    val description: String,
    val rationaleMessage: String
) {
    data object Notification : PermissionType(
        permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else "",
        title = "Thông báo",
        description = "Nhận thông báo từ ứng dụng",
        rationaleMessage = "Để nhận thông báo quan trọng từ ứng dụng, bạn cần cấp quyền thông báo."
    )
    fun isGranted(context: Context): Boolean {
        return when (this) {
            is Notification -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    ContextCompat.checkSelfPermission(
                        context,
                        permission
                    ) == PackageManager.PERMISSION_GRANTED
                } else {
                    NotificationManagerCompat.from(context).areNotificationsEnabled()
                }
            }
            else -> {
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            }
        }
    }

    fun getSettingsIntent(context: Context): Intent {
        return when (this) {
            is Notification -> {
                Intent().apply {
                    action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                }
            }
            else -> {
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
            }
        }
    }

}