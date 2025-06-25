package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jerry.ronaldo.siufascore.presentation.highlight.DetailHighlightViewModel
import com.jerry.ronaldo.siufascore.presentation.highlight.screen.HighLightDetailScreen
import kotlinx.serialization.Serializable

@Serializable
data class HighlightNavigationRoute(
    val videoId: String
)

fun NavController.navigateToDetailHighlight(
    videoId: String,
    navOptions: NavOptions? = null
) {
    navigate(route = HighlightNavigationRoute(videoId), navOptions)
}

fun NavGraphBuilder.detailHighlightScreen(
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onVideoClick: (String) -> Unit,
) {
    composable<HighlightNavigationRoute> { entry ->
        val videoId = entry.toRoute<HighlightNavigationRoute>().videoId
        HighLightDetailScreen(
            viewModel = hiltViewModel<DetailHighlightViewModel, DetailHighlightViewModel.Factory>(
                key = videoId
            ) { factory ->
                factory.create(videoId = videoId)
            },
        )
    }
}