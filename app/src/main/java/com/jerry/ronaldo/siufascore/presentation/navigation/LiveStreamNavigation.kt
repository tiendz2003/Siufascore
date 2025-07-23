package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.jerry.ronaldo.siufascore.presentation.livestream.LiveStreamScreen
import kotlinx.serialization.Serializable

@Serializable
data object LiveStreamRoute

fun NavController.navigateToLiveStreamScreen(navOptions:NavOptions){
    navigate(route = LiveStreamRoute, navOptions)
}
fun NavGraphBuilder.liveStreamScreen(
    onClick:(Int)-> Unit
){
    composable<LiveStreamRoute>{
        LiveStreamScreen(
            onClick = { matchId ->
                onClick(matchId)
            }
        )
    }
}