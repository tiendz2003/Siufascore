package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import com.jerry.ronaldo.siufascore.presentation.ui.AppState

@Composable
fun AppNavHost(
    appState: AppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    NavHost(
        navController = navController,
        startDestination = HomeBaseRoute,
        modifier = modifier,
    ) {
        homeSection(
            navController = navController,
        )
        highlightScreen(
            onVideoClick = { videoId ->
                navController.navigateToDetailHighlight(videoId)
            }
        )
        detailMatchScreen(
            onBackClick = navController::popBackStack,
            onMatchClick = {},
            onTeamClick = { }
        )
        detailHighlightScreen(
            showBackButton = true,
            onBackClick = navController::popBackStack,
            onVideoClick = {
            }
        )
        newsScreen(
            onNewsClick = { uriNews ->
                navController.navigateToDetailNews(uriNews)
            }
        )
        detailNewsScreen(
            onBackClick = navController::popBackStack,
            onShareClick = { }
        )
    }
}