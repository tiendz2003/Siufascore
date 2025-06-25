package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jerry.ronaldo.siufascore.presentation.player.DetailPlayerScreen
import com.jerry.ronaldo.siufascore.presentation.player.DetailPlayerViewModel
import com.jerry.ronaldo.siufascore.presentation.search.screen.SearchScreen
import com.jerry.ronaldo.siufascore.presentation.team.DetailTeamScreen
import com.jerry.ronaldo.siufascore.presentation.team.DetailTeamViewModel
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute

fun NavGraphBuilder.searchScreen(
    onPlayerClick: (Int) -> Unit,
    onTeamClick: (Int,Int) -> Unit,
    onBackClick: () -> Unit
) {
    composable<SearchRoute> {
        SearchScreen(
            onBackClick = onBackClick,
            onPlayerClick = { id->
                onPlayerClick(id)
            },
            onTeamClick = { id,league->
                onTeamClick(id,league)
            }
        )
    }
}

fun NavController.navigateToSearch(
    navOptions: NavOptions? = null
) {
    navigate(route = SearchRoute, navOptions)
}

@Serializable
data class DetailPlayerRoute(val playerId: Int)

fun NavController.navigateToDetailPlayerScreen(
    id: Int,
    navOptions: NavOptions? = null
) {
    navigate(route = DetailPlayerRoute(id), navOptions)
}

fun NavGraphBuilder.detailPlayerScreen(
    onBackClick: () -> Unit,
) {
    composable<DetailPlayerRoute> { entry ->
        val id = entry.toRoute<DetailPlayerRoute>().playerId
        //Màn hình detail search
        DetailPlayerScreen(
            viewModel = hiltViewModel<DetailPlayerViewModel,DetailPlayerViewModel.Factory>(){factory->
                factory.create(id)
            },
            onBackClick = onBackClick
        )

    }
}

@Serializable
data class DetailTeamRoute(val teamId: Int,val leagueId:Int)

fun NavController.navigateToDetailTeamScreen(
    teamId: Int,
    leagueId:Int,
    navOptions: NavOptions? = null
) {
    navigate(route = DetailTeamRoute(teamId,leagueId), navOptions)
}

fun NavGraphBuilder.detailTeamScreen(
    onBackClick: () -> Unit,
    onPlayerClick:(Int) ->Unit
) {
    composable<DetailTeamRoute> { entry ->
        val teamId = entry.toRoute<DetailTeamRoute>().teamId
        val leagueId = entry.toRoute<DetailTeamRoute>().leagueId
        DetailTeamScreen(
            viewModel = hiltViewModel<DetailTeamViewModel,DetailTeamViewModel.Factory> { factory->
                factory.create(teamId,leagueId)
            },
            onBackClick = onBackClick,
            onPlayerClick = {id->
                onPlayerClick(id)
            }
        )

    }
}
