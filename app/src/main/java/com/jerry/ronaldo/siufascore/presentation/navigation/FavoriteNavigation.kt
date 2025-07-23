package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jerry.ronaldo.siufascore.presentation.favorite.FavoriteScreen
import kotlinx.serialization.Serializable

@Serializable
data object FavoriteRoute

fun NavController.navigateToFavorite(navOptions: NavOptions) =
    navigate(route = FavoriteRoute, navOptions)

fun NavGraphBuilder.favoriteScreen(
    onTeamClick: (Int, Int) -> Unit
) {
    composable<FavoriteRoute> {
        FavoriteScreen(
            onTeamClick = { teamId, leagueId ->
                onTeamClick(teamId, leagueId)
            },
        )
    }
}