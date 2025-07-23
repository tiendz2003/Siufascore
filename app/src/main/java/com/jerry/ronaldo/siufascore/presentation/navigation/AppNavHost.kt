package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.jerry.ronaldo.siufascore.presentation.ui.AppState

@Composable
fun AppNavHost(
    appState: AppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    val isAuthenticated = appState.isAuthenticated
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) HomeBaseRoute else AuthRoute,
        modifier = modifier,
    ) {
        loginScreen(
            onSignUpClick = {
                navController.navigateToSignUp()
            },
            onSignInClick = {},
            onSuccess = {
                navController.navigateToHome(
                    navOptions = navOptions {
                        popUpTo(AuthRoute) {
                            inclusive = true
                        }
                    }
                )
            }
        )
        signUpScreen(
            onSignUpClick = { },
            onSuccess = {
                navController.navigateToLogin(
                    navOptions = navOptions {
                        popUpTo(SignUpRoute) {
                            inclusive = true
                        }
                    }
                )
            }
        )

        homeSection(
            onMatchClick = { matchId ->
                navController.navigateToDetailMatch(
                    matchId = matchId
                )
            }
        )
        favoriteScreen(
            onTeamClick = { teamId, leagueId ->
                navController.navigateToDetailTeamScreen(
                    teamId = teamId,
                    leagueId = leagueId
                )
            }
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

        searchScreen(
            onPlayerClick = { id ->
                navController.navigateToDetailPlayerScreen(id)
            },
            onTeamClick = { teamId, leagueId ->
                navController.navigateToDetailTeamScreen(teamId, leagueId)
            },
            onBackClick = navController::popBackStack
        )

        detailPlayerScreen(
            onBackClick = navController::popBackStack
        )
        detailTeamScreen(
            onBackClick = navController::popBackStack,
            onPlayerClick = { playerId ->
                navController.navigateToDetailPlayerScreen(playerId)
            }
        )
    }
}