package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.jerry.ronaldo.siufascore.presentation.highlight.DetailHighlightViewModel
import com.jerry.ronaldo.siufascore.presentation.highlight.screen.HighLightDetailScreen
import com.jerry.ronaldo.siufascore.utils.NavigationTransitionResolver
import com.jerry.ronaldo.siufascore.utils.animComposable
import kotlinx.serialization.Serializable

@Serializable
data class DetailHighlightRoute(
    val videoId: String
)

fun NavController.navigateToDetailHighlight(
    videoId: String,
    navOptions: NavOptions? = null
) {
    navigate(route = DetailHighlightRoute(videoId), navOptions)
}

fun NavGraphBuilder.detailHighlightScreen(
    transitionResolver: NavigationTransitionResolver,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onVideoClick: (String) -> Unit,
) {
    animComposable<DetailHighlightRoute>(transitionResolver) { route ->
        HighLightDetailScreen(
            viewModel = hiltViewModel<DetailHighlightViewModel, DetailHighlightViewModel.Factory>(
                key = route.videoId
            ) { factory ->
                factory.create(videoId = route.videoId)
            },
        )
    }
}