package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.jerry.ronaldo.siufascore.presentation.livestream.LiveStreamScreen
import com.jerry.ronaldo.siufascore.utils.NavigationTransitionResolver
import com.jerry.ronaldo.siufascore.utils.animComposable
import kotlinx.serialization.Serializable

@Serializable
data object LiveStreamRoute

fun NavController.navigateToLiveStreamScreen(navOptions:NavOptions){
    navigate(route = LiveStreamRoute, navOptions)
}
fun NavGraphBuilder.liveStreamScreen(
    transitionResolver: NavigationTransitionResolver,
    onClick:(Int)-> Unit
){
    animComposable<LiveStreamRoute>(
        transitionResolver
    ){
        LiveStreamScreen(
            onClick = { matchId ->
                onClick(matchId)
            }
        )
    }
}