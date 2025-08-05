package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.jerry.ronaldo.siufascore.presentation.setting.SettingScreen
import com.jerry.ronaldo.siufascore.utils.NavigationTransitionResolver
import com.jerry.ronaldo.siufascore.utils.animComposable
import kotlinx.serialization.Serializable

@Serializable
data object SettingRoute:AppRoute

fun NavGraphBuilder.settingScreen(
    transitionResolver: NavigationTransitionResolver,
    onBackClick: () -> Unit,
    onSignOut: () -> Unit
) {
    animComposable<SettingRoute>(
        transitionResolver = transitionResolver,
    ) {
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
