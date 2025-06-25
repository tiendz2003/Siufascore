package com.jerry.ronaldo.siufascore.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.jerry.ronaldo.siufascore.presentation.navigation.TopLevelDestination
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToHighlight
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToHome
import com.jerry.ronaldo.siufascore.presentation.navigation.navigateToNews
import com.jerry.ronaldo.siufascore.utils.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@Composable
fun rememberAppState(
    networkMonitor: NetworkMonitor,
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
            navController = navController
        )
    }
}

@Stable
class AppState(
    val navController: NavHostController,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor
) {
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
    val isOffline =networkMonitor.isOnline.map(Boolean::not).stateIn(
        scope =coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )
    val topLevelDestination:List<TopLevelDestination> =TopLevelDestination.entries

    fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination){
        val topLevelNavOptions = navOptions {
            popUpTo(navController.graph.findStartDestination().id){
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
        when(topLevelDestination){
            TopLevelDestination.HOME -> navController.navigateToHome(topLevelNavOptions)
            TopLevelDestination.HIGHLIGHT -> navController.navigateToHighlight(topLevelNavOptions)
            TopLevelDestination.NEWS -> {navController.navigateToNews(topLevelNavOptions)}
            TopLevelDestination.LIVES_STREAM -> {}
        }
    }

}