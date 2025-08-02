package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jerry.ronaldo.siufascore.presentation.setting.SettingScreen
import kotlinx.serialization.Serializable

@Serializable
data object SettingRoute

fun NavGraphBuilder.settingScreen(
    onBackClick: () -> Unit,
    onSignOut: () -> Unit
) {
    composable<SettingRoute> {
        SettingScreen(
            onBackClick = onBackClick,
            onSignOut = {
                onSignOut()
            },
        )
    }
}
fun NavController.navigateToSetting(
    navOptions: NavOptions? = null
) {
    navigate(route = SettingRoute, navOptions)
}
