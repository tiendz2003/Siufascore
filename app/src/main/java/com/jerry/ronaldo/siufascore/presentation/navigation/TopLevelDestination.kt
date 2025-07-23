package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LiveTv
import androidx.compose.material.icons.outlined.Newspaper
import androidx.compose.material.icons.outlined.Tv
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
    FAVORITE(
        selectedIcon = Icons.Default.Favorite,
        unselectedIcon = Icons.Outlined.Favorite,
        iconTextId = R.string.favorite,
        titleTextId = R.string.favorite,
        route = FavoriteRoute::class,
    ),
    HIGHLIGHT(
        selectedIcon = Icons.Default.Tv,
        unselectedIcon = Icons.Outlined.Tv,
        iconTextId = R.string.highlight,
        titleTextId = R.string.highlight,
        route = HighlightRoute::class,
    ),
    NEWS(
        selectedIcon = Icons.Default.Newspaper,
        unselectedIcon = Icons.Outlined.Newspaper,
        iconTextId = R.string.news,
        titleTextId = R.string.news,
        route = NewsRoute::class,
    ),
    LIVES_STREAM(
        selectedIcon = Icons.Default.LiveTv,
        unselectedIcon = Icons.Outlined.LiveTv,
        iconTextId = R.string.live_stream,
        titleTextId = R.string.live_stream,
        route = HighlightRoute::class,
    ),
}