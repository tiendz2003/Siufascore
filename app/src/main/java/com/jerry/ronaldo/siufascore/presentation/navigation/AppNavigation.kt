package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jerry.ronaldo.siufascore.presentation.highlight.screen.ListHighLightsScreen
import com.jerry.ronaldo.siufascore.presentation.matches.screen.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeRoute

@Serializable
data object HomeBaseRoute

fun NavController.navigateToHome(navOptions: NavOptions) = navigate(route = HomeRoute, navOptions)
fun NavGraphBuilder.homeSection(
    onMatchClick: (Int) -> Unit,
) {
    navigation<HomeBaseRoute>(startDestination = HomeRoute) {
        composable<HomeRoute> {
            HomeScreen(
                onMatchClick = { matchId ->
                    onMatchClick(matchId)
                }
            )
        }
    }
}

@Serializable
data object HighlightRoute

fun NavController.navigateToHighlight(navOptions: NavOptions) =
    navigate(route = HighlightRoute, navOptions)

fun NavGraphBuilder.highlightScreen(
    onVideoClick: (String) -> Unit
) {
    composable<HighlightRoute> {
        ListHighLightsScreen(
            onVideoClick = {
                onVideoClick(it)
            },
        )
    }
}