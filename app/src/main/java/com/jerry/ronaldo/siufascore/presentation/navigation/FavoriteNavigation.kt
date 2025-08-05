package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.jerry.ronaldo.siufascore.presentation.favorite.FavoriteScreen
import com.jerry.ronaldo.siufascore.utils.NavigationTransitionResolver
import com.jerry.ronaldo.siufascore.utils.animComposable
import kotlinx.serialization.Serializable

@Serializable
data object FavoriteRoute

fun NavController.navigateToFavorite(navOptions: NavOptions) =
    navigate(route = FavoriteRoute, navOptions)

fun NavGraphBuilder.favoriteScreen(
    transitionResolver: NavigationTransitionResolver,
    onTeamClick: (Int, Int) -> Unit,
    onPlayerClick: (Int) -> Unit
) {
    animComposable<FavoriteRoute>(
        transitionResolver
    ) {
        FavoriteScreen(
            onTeamClick = { teamId, leagueId ->
                onTeamClick(teamId, leagueId)
            },
            onPlayerClick = { playerId ->
                onPlayerClick(playerId)
            }
        )
    }
}