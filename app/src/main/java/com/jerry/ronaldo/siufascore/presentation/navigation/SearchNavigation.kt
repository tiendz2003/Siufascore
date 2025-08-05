package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.jerry.ronaldo.siufascore.presentation.player.DetailPlayerScreen
import com.jerry.ronaldo.siufascore.presentation.player.DetailPlayerViewModel
import com.jerry.ronaldo.siufascore.presentation.search.screen.SearchScreen
import com.jerry.ronaldo.siufascore.presentation.team.DetailTeamScreen
import com.jerry.ronaldo.siufascore.presentation.team.DetailTeamViewModel
import com.jerry.ronaldo.siufascore.utils.NavigationTransitionResolver
import com.jerry.ronaldo.siufascore.utils.animComposable
import kotlinx.serialization.Serializable

@Serializable
data object SearchRoute:AppRoute

fun NavGraphBuilder.searchScreen(
    transitionResolver: NavigationTransitionResolver,
    onPlayerClick: (Int) -> Unit,
    onTeamClick: (Int,Int) -> Unit,
    onBackClick: () -> Unit
) {
    animComposable<SearchRoute>(transitionResolver) {
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
data class DetailPlayerRoute(val playerId: Int):AppRoute

fun NavController.navigateToDetailPlayerScreen(
    id: Int,
    navOptions: NavOptions? = null
) {
    navigate(route = DetailPlayerRoute(id), navOptions)
}

fun NavGraphBuilder.detailPlayerScreen(
    transitionResolver: NavigationTransitionResolver,
    onBackClick: () -> Unit,
) {
    animComposable<DetailPlayerRoute>(
        transitionResolver = transitionResolver
    ) { route->
        //Màn hình detail search
        DetailPlayerScreen(
            viewModel = hiltViewModel<DetailPlayerViewModel,DetailPlayerViewModel.Factory>(){factory->
                factory.create(route.playerId)
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
    transitionResolver: NavigationTransitionResolver,
    onBackClick: () -> Unit,
    onPlayerClick:(Int) ->Unit
) {
    animComposable<DetailTeamRoute>(transitionResolver) { route ->
        val teamId = route.teamId
        val leagueId = route.leagueId
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
