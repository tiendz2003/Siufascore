package com.jerry.ronaldo.siufascore.presentation.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.designsystem.component.AppBackground
import com.jerry.ronaldo.siufascore.designsystem.component.AppGradientBackground
import com.jerry.ronaldo.siufascore.presentation.navigation.AppNavHost
import com.jerry.ronaldo.siufascore.presentation.navigation.TopLevelDestination
import kotlin.reflect.KClass

@Composable
fun SiufascoreApp(
    appState: AppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo()
) {
    val shouldShowGradientBackground =
        appState.currentTopLevelDestination == TopLevelDestination.HIGHLIGHT
    AppBackground(modifier = modifier) {
        AppGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            }
        ) {
            val snackBarHostState = remember { SnackbarHostState() }
            val isOffline by appState.isOffline.collectAsStateWithLifecycle()
            val notConnectedMessage = stringResource(R.string.not_connected)
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackBarHostState.showSnackbar(
                        message = notConnectedMessage,
                        duration = SnackbarDuration.Indefinite
                    )
                }
            }
            SiufaScoreApp(
                appState = appState,
                snackbarHostState = snackBarHostState,
                onTopAppBarActionClick = { },
                modifier = modifier,
                windowAdaptiveInfo = windowAdaptiveInfo
            )
        }
    }
}

@Composable
fun SiufaScoreApp(
    appState: AppState,
    snackbarHostState: SnackbarHostState,
    onTopAppBarActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    val currentDestination = appState.currentDestination
    val shouldShowBottomNavigation = appState.shouldShowBottomNavigation
    val shouldShowTopAppBar = appState.currentTopLevelDestination != null

    // Chỉ hiển thị navigation suite khi cần thiết
    if (shouldShowBottomNavigation) {
        AppNavigationSuiteScaffold(
            navigationSuiteItems = {
                appState.topLevelDestination.forEach { destination ->
                    val selected = currentDestination.isRouteInHierarchy(destination.baseRoute)
                    item(
                        selected = selected,
                        onClick = {
                            appState.navigateToTopLevelDestination(destination)
                        },
                        icon = {
                            AnimationNavigationIcon(
                                iconRes = destination.unselectedIcon,
                                selected = false,
                                label = "icon_unselected"
                            )
                        },
                        selectedIcon = {
                            AnimationNavigationIcon(
                                selectedIconRes = destination.selectedIcon,
                                selected = true,
                                label = "icon_selected"
                            )
                        },
                        label = {
                            AnimationNavigationLabel(
                                text = stringResource(destination.iconTextId),
                                selected = selected
                            )
                        },
                        modifier = Modifier.testTag("AppNavItem")
                    )
                }
            },
            windowAdaptiveInfo = windowAdaptiveInfo,
        ) {
            AppScaffoldContent(
                appState = appState,
                snackbarHostState = snackbarHostState,
                onTopAppBarActionClick = onTopAppBarActionClick,
                shouldShowTopAppBar = shouldShowTopAppBar,
                modifier = modifier
            )
        }
    } else {
        // Không hiển thị bottom navigation
        AppScaffoldContent(
            appState = appState,
            snackbarHostState = snackbarHostState,
            onTopAppBarActionClick = onTopAppBarActionClick,
            shouldShowTopAppBar = shouldShowTopAppBar,
            modifier = modifier
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppScaffoldContent(
    appState: AppState,
    snackbarHostState: SnackbarHostState,
    onTopAppBarActionClick: () -> Unit,
    shouldShowTopAppBar: Boolean,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.semantics {
            testTagsAsResourceId = true
        },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = {
            SnackbarHost(
                snackbarHostState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            if (shouldShowTopAppBar) {
                val destination = appState.currentTopLevelDestination
                if (destination != null) {
                    TopAppBar(
                        titleRes = destination.titleTextId,
                        navigationIcon = Icons.Default.Search,
                        navigationIconContentDescription = "Search",
                        actionIcon = Icons.Default.Settings,
                        actionIconContentDescription = "Setting",
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = PremierPurpleDark
                        ),
                        onNavigationClick = {
                            appState.navigateToSearch()
                        },
                        modifier = modifier,
                        onActionClick = {
                            onTopAppBarActionClick()
                        }
                    )
                }
            }

            Box(
                modifier = Modifier.weight(1f).consumeWindowInsets(
                    if (shouldShowTopAppBar) {
                        WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                    } else {
                        WindowInsets(0, 0, 0, 0)
                    }
                )
            ) {
                AppNavHost(
                    appState = appState,
                    onShowSnackbar = { message, action ->
                        snackbarHostState.showSnackbar(
                            message = message,
                            actionLabel = action,
                            duration = SnackbarDuration.Short
                        ) == SnackbarResult.ActionPerformed
                    }
                )
            }
        }
    }
}

private fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
    this?.hierarchy?.any {
        it.hasRoute(route)
    } ?: false
