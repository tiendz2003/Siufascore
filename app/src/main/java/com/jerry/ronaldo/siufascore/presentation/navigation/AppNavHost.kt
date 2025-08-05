package com.jerry.ronaldo.siufascore.presentation.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.navOptions
import com.jerry.ronaldo.siufascore.presentation.LiveStreamPlayerActivity
import com.jerry.ronaldo.siufascore.presentation.ui.AppState
import com.jerry.ronaldo.siufascore.utils.NavigationTransitionResolver

@Composable
fun AppNavHost(
    appState: AppState,
    onShowSnackbar: suspend (String, String?) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val navController = appState.navController
    val isAuthenticated = appState.isAuthenticated
    val context = LocalContext.current
    val transitionResolver = remember { NavigationTransitionResolver() }
    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) HomeBaseRoute else AuthRoute,
        modifier = modifier,
    ) {
        loginScreen(
            transitionResolver = transitionResolver,
            onSignUpClick = {
                navController.navigateToSignUp()
            },
            onSignInClick = {},
            onSuccess = {
                navController.navigateToHome(
                    navOptions = navOptions {
                        // xóa khỏi backstack khi đăng nhập thành công
                        popUpTo(AuthRoute) {
                            inclusive = true
                        }
                    }
                )
            }
        )
        signUpScreen(
            transitionResolver = transitionResolver,
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
            transitionResolver = transitionResolver,
            onTeamClick = { teamId, leagueId ->
                navController.navigateToDetailTeamScreen(
                    teamId = teamId,
                    leagueId = leagueId
                )
            },
            onPlayerClick = { playerId ->
                navController.navigateToDetailPlayerScreen(playerId)
            }
        )
        highlightScreen(
            transitionResolver = transitionResolver,
            onVideoClick = { videoId ->
                navController.navigateToDetailHighlight(videoId)
            }
        )

        detailMatchScreen(
            transitionResolver = transitionResolver,
            onBackClick = navController::popBackStack,
            onMatchClick = {},
            onTeamClick = { }
        )

        detailHighlightScreen(
            transitionResolver = transitionResolver,
            showBackButton = true,
            onBackClick = navController::popBackStack,
            onVideoClick = {
            }
        )

        newsScreen(
            transitionResolver = transitionResolver,
            onNewsClick = { uriNews ->
                navController.navigateToDetailNews(uriNews)
            }
        )

        detailNewsScreen(
            transitionResolver = transitionResolver,
            onBackClick = navController::popBackStack,
            onShareClick = { }
        )

        liveStreamScreen(
            transitionResolver = transitionResolver,
            onClick = { matchId ->
                val intent = Intent(context, LiveStreamPlayerActivity::class.java).apply {
                    putExtra("MATCH_ID", matchId)
                }
                context.startActivity(intent)
            }
        )
        searchScreen(
            transitionResolver = transitionResolver,
            onPlayerClick = { id ->
                navController.navigateToDetailPlayerScreen(id)
            },
            onTeamClick = { teamId, leagueId ->
                navController.navigateToDetailTeamScreen(teamId, leagueId)
            },
            onBackClick = navController::popBackStack
        )
        settingScreen(
            transitionResolver = transitionResolver,
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
            transitionResolver = transitionResolver,
            onBackClick = navController::popBackStack
        )
        detailTeamScreen(
            transitionResolver = transitionResolver,
            onBackClick = navController::popBackStack,
            onPlayerClick = { playerId ->
                navController.navigateToDetailPlayerScreen(playerId)
            }
        )
    }
}