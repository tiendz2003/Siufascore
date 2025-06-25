package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jerry.ronaldo.siufascore.presentation.matches.DetailMatchViewModel
import com.jerry.ronaldo.siufascore.presentation.matches.screen.detail.DetailMatchScreen
import kotlinx.serialization.Serializable

@Serializable
data class DetailMatchRoute(val matchId: Int)

fun NavController.navigateToDetailMatch(navOptions: NavOptions? = null, matchId: Int) {
    navigate(DetailMatchRoute(matchId), navOptions)
}

fun NavGraphBuilder.detailMatchScreen(
    onBackClick: () -> Unit,
    onMatchClick: (Int) -> Unit,
    onTeamClick: (Int) -> Unit
) {
    composable<DetailMatchRoute> { entry ->
        val matchId = entry.toRoute<DetailMatchRoute>().matchId
        //Màn hình detailMatch
        DetailMatchScreen(
            onBackClick = onBackClick,
            detailViewModel = hiltViewModel<DetailMatchViewModel, DetailMatchViewModel.Factory> { factory ->
                factory.create(matchId)
            }
        )
    }
}