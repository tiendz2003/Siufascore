package com.jerry.ronaldo.siufascore.utils

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import com.jerry.ronaldo.siufascore.presentation.navigation.AuthRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.DetailHighlightRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.DetailNewsRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.DetailPlayerRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.DetailTeamRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.FavoriteRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.HighlightRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.HomeRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.LiveStreamRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.NewsRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.SearchRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.SettingRoute
import com.jerry.ronaldo.siufascore.presentation.navigation.SignUpRoute

object ScreenTransitions {

    // Slide horizontal (for detail navigation)
    fun slideHorizontal(
        duration: Int = 300,
        easing: Easing = FastOutSlowInEasing
    ): ContentTransform {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(duration, easing = easing)
        ) + fadeIn(
            animationSpec = tween(duration)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(duration, easing = easing)
        ) + fadeOut(
            animationSpec = tween(duration)
        )
    }

    // Slide horizontal reverse (for back navigation)
    fun slideHorizontalReverse(
        duration: Int = 300,
        easing: Easing = FastOutSlowInEasing
    ): ContentTransform {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(duration, easing = easing)
        ) + fadeIn(
            animationSpec = tween(duration)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(duration, easing = easing)
        ) + fadeOut(
            animationSpec = tween(duration)
        )
    }

    // Slide up (for modal)
    fun slideUp(
        duration: Int = 400,
        easing: Easing = FastOutSlowInEasing
    ): ContentTransform {
        return slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(duration, easing = easing)
        ) + fadeIn(
            animationSpec = tween(duration)
        ) togetherWith slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight / 4 },
            animationSpec = tween(duration, easing = easing)
        ) + fadeOut(
            animationSpec = tween(duration)
        )
    }

    // Slide down (for modal dismiss)
    fun slideDown(
        duration: Int = 400,
        easing: Easing = FastOutSlowInEasing
    ): ContentTransform {
        return slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight / 4 },
            animationSpec = tween(duration, easing = easing)
        ) + fadeIn(
            animationSpec = tween(duration)
        ) togetherWith slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(duration, easing = easing)
        ) + fadeOut(
            animationSpec = tween(duration)
        )
    }

    // Scale (for settings/profile)
    fun scale(
        duration: Int = 300,
        easing: Easing = FastOutSlowInEasing
    ): ContentTransform {
        return scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(duration, easing = easing)
        ) + fadeIn(
            animationSpec = tween(duration)
        ) togetherWith scaleOut(
            targetScale = 1.1f,
            animationSpec = tween(duration, easing = easing)
        ) + fadeOut(
            animationSpec = tween(duration)
        )
    }

    // Scale reverse
    fun scaleReverse(
        duration: Int = 300,
        easing: Easing = FastOutSlowInEasing
    ): ContentTransform {
        return scaleIn(
            initialScale = 1.1f,
            animationSpec = tween(duration, easing = easing)
        ) + fadeIn(
            animationSpec = tween(duration)
        ) togetherWith scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(duration, easing = easing)
        ) + fadeOut(
            animationSpec = tween(duration)
        )
    }

    // Fade (for tab navigation)
    fun fade(
        duration: Int = 250
    ): ContentTransform {
        return fadeIn(
            animationSpec = tween(duration)
        ) togetherWith fadeOut(
            animationSpec = tween(duration)
        )
    }

    // Shared axis (for statistics)
    fun sharedAxisX(
        duration: Int = 350,
        easing: Easing = FastOutSlowInEasing
    ): ContentTransform {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(duration, easing = easing)
        ) + fadeIn(
            animationSpec = tween(duration, delayMillis = 90)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 3 },
            animationSpec = tween(duration, easing = easing)
        ) + fadeOut(
            animationSpec = tween(duration)
        )
    }

    // Shared axis reverse
    fun sharedAxisXReverse(
        duration: Int = 350,
        easing: Easing = FastOutSlowInEasing
    ): ContentTransform {
        return slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth / 3 },
            animationSpec = tween(duration, easing = easing)
        ) + fadeIn(
            animationSpec = tween(duration, delayMillis = 90)
        ) togetherWith slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(duration, easing = easing)
        ) + fadeOut(
            animationSpec = tween(duration)
        )
    }
}

sealed class RouteCategory {
    data object Tab : RouteCategory()
    data object Detail : RouteCategory()
    data object Settings : RouteCategory()
    data object Search : RouteCategory()
    data object Auth : RouteCategory()
}

object RouteClassifier {
    fun classifyRoute(route: Any): RouteCategory {
        return when (route) {
            is AuthRoute, is SignUpRoute -> RouteCategory.Auth
            // Tab routes
            is HomeRoute, is HighlightRoute, is FavoriteRoute, is LiveStreamRoute, is NewsRoute ->
                RouteCategory.Tab
            // Detail routes
            is DetailHighlightRoute, is DetailNewsRoute, is DetailPlayerRoute, is DetailTeamRoute ->
                RouteCategory.Detail

            is SearchRoute -> RouteCategory.Search
            is SettingRoute -> RouteCategory.Settings
            else -> RouteCategory.Detail
        }
    }

    fun isBackNavigation(fromRoute: Any?, toRoute: Any): Boolean {
        // Logic để xác định có phải back navigation không
        val fromCategory = fromRoute?.let { classifyRoute(it) }
        val toCategory = classifyRoute(toRoute)
        return when {
            // Từ Detail về Tab là back
            fromCategory is RouteCategory.Detail && toCategory is RouteCategory.Tab -> true
            // Từ Search về bất cứ đâu là back
            fromCategory is RouteCategory.Search -> true
            // Từ Settings về bất cứ đâu là back
            fromCategory is RouteCategory.Settings -> true
            // Thêm các trường hợp khác nếu cần
            else -> false
        }
    }
}

