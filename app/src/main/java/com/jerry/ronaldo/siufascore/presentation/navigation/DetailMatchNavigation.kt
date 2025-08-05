package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.jerry.ronaldo.siufascore.presentation.matches.DetailMatchViewModel
import com.jerry.ronaldo.siufascore.presentation.matches.screen.detail.DetailMatchScreen
import com.jerry.ronaldo.siufascore.utils.NavigationTransitionResolver
import com.jerry.ronaldo.siufascore.utils.animComposable
import kotlinx.serialization.Serializable

@Serializable
data class DetailMatchRoute(val matchId: Int)

fun NavController.navigateToDetailMatch(navOptions: NavOptions? = null, matchId: Int) {
    navigate(DetailMatchRoute(matchId), navOptions)
}

fun NavGraphBuilder.detailMatchScreen(
    transitionResolver: NavigationTransitionResolver,
    onBackClick: () -> Unit,
    onMatchClick: (Int) -> Unit,
    onTeamClick: (Int) -> Unit
) {
    animComposable<DetailMatchRoute>(transitionResolver) { route ->
        val matchId = route.matchId
        //Màn hình detailMatch
        DetailMatchScreen(
            onBackClick = onBackClick,
            detailViewModel = hiltViewModel<DetailMatchViewModel, DetailMatchViewModel.Factory> { factory ->
                factory.create(matchId)
            }
        )
    }
}