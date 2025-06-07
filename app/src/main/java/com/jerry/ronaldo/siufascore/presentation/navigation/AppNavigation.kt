package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.jerry.ronaldo.siufascore.presentation.matches.screen.home.HomeScreen
import com.jerry.ronaldo.siufascore.presentation.matches.screen.home.StandingScreen
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
            HomeScreen(onMatchClick)
        }
    }
}

@Serializable
data object NewsRoute

fun NavController.navigateToNews(navOptions: NavOptions) = navigate(route = NewsRoute, navOptions)
fun NavGraphBuilder.newsScreen() {
    composable<NewsRoute> {
        StandingScreen()
    }
}