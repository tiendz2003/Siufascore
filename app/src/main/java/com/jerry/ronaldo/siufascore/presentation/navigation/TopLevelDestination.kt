package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector
import com.jerry.ronaldo.siufascore.R
import kotlin.reflect.KClass

enum class TopLevelDestination(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
    val route: KClass<*>,
    val baseRoute: KClass<*> = route,
) {
    HOME(
        selectedIcon = Icons.Default.Home,
        unselectedIcon = Icons.Outlined.Home,
        iconTextId = R.string.home,
        titleTextId = R.string.home,
        route = HomeRoute::class,
        baseRoute = HomeBaseRoute::class,
    ),
    NEWS(
        selectedIcon = Icons.Default.AccountCircle,
        unselectedIcon = Icons.Outlined.AccountCircle,
        iconTextId = R.string.news,
        titleTextId = R.string.news,
        route = NewsRoute::class,
    ),
    LIVES_STREAM(
        selectedIcon = Icons.Default.PlayArrow,
        unselectedIcon = Icons.Outlined.PlayArrow,
        iconTextId = R.string.live_stream,
        titleTextId = R.string.live_stream,
        route = NewsRoute::class,
    ),
}