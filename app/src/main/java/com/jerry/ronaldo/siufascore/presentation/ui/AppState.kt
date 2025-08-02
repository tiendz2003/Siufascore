package com.jerry.ronaldo.siufascore.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.jerry.ronaldo.siufascore.data.remote.AuthRepository
import com.jerry.ronaldo.siufascore.presentation.navigation.TopLevelDestination
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToFavorite
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToHighlight
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToHome
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToLiveStreamScreen
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToNews
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToSearch
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToSetting
import com.jerry.ronaldo.siufascore.utils.AuthResult
import com.jerry.ronaldo.siufascore.utils.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberAppState(
    networkMonitor: NetworkMonitor,
    authRepository: AuthRepository,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    navController: NavHostController = rememberNavController()
): AppState {
    return remember(
        networkMonitor,
        coroutineScope,
        navController
    ) {
        AppState(
            networkMonitor = networkMonitor,
            coroutineScope = coroutineScope,
            navController = navController,
            authRepository = authRepository
        )
    }
}

@Stable
class AppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    private val authRepository: AuthRepository
) {
    private val authState: StateFlow<AuthResult> = authRepository
        .observeAuthState()
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = AuthResult.Loading
        )

    val isAuthenticated: Boolean
        @Composable get() = authState.collectAsStateWithLifecycle().value is AuthResult.AuthenticatedSuccess
    private val previousDestination = mutableStateOf<NavDestination?>(null)
    val currentDestination: NavDestination?
        @Composable get() {
            val currentEntry =
                navController.currentBackStackEntryFlow.collectAsState(initial = null)
            return currentEntry.value?.destination.also { destination ->
                if (destination != null) {
                    previousDestination.value = destination
                }
            } ?: previousDestination.value
        }
    val currentTopLevelDestination: TopLevelDestination?
        @Composable get() {
            return TopLevelDestination.entries.firstOrNull { topLevelDestination ->
                currentDestination?.hasRoute(
                    route = topLevelDestination.route
                ) == true
            }
        }
    val isOffline = networkMonitor.isOnline.map(Boolean::not).stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )
    val topLevelDestination: List<TopLevelDestination> = TopLevelDestination.entries
    val shouldShowBottomNavigation: Boolean
        @Composable get() {
            val currentRoute = currentDestination?.route
            return currentRoute != null && topLevelDestination.any { destination ->
                currentDestination?.hasRoute(destination.route) == true
            }
        }

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        when (topLevelDestination) {
            TopLevelDestination.HOME -> navController.navigateToHome(topLevelNavOptions)
            TopLevelDestination.FAVORITE -> navController.navigateToFavorite(topLevelNavOptions)
            TopLevelDestination.HIGHLIGHT -> navController.navigateToHighlight(topLevelNavOptions)
            TopLevelDestination.NEWS -> navController.navigateToNews(topLevelNavOptions)
            TopLevelDestination.LIVES_STREAM -> {
                navController.navigateToLiveStreamScreen(topLevelNavOptions)
            }
        }
    }

    fun navigateToSearch() = navController.navigateToSearch()
    fun navigateToSetting() = navController.navigateToSetting()
}