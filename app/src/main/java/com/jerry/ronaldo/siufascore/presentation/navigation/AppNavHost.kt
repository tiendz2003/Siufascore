package com.jerry.ronaldo.siufascore.presentation.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.jerry.ronaldo.siufascore.presentation.LiveStreamPlayerActivity
import com.jerry.ronaldo.siufascore.presentation.ui.AppState

@Composable
fun AppNavHost(
    appState: AppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    val isAuthenticated = appState.isAuthenticated
    val context = LocalContext.current
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

        liveStreamScreen(

            onClick = { matchId ->
                val intent = Intent(context,LiveStreamPlayerActivity::class.java).apply {
                    putExtra("MATCH_ID", matchId)
                }
                context.startActivity(intent)
            }
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
        settingScreen (
            onBackClick = navController::popBackStack,
            onSignOut = {
                navOptions {
                    popUpTo(navController.graph.findStartDestination().id) {
                        inclusive = true
                    }
                }.let { options ->
                    navController.navigateToLogin(navOptions = options)
                }
            }
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