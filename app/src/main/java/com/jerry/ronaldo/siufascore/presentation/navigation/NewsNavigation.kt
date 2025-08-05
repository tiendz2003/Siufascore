package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import com.jerry.ronaldo.siufascore.presentation.news.NewsDetailViewModel
import com.jerry.ronaldo.siufascore.presentation.news.screen.NewsDetailScreen
import com.jerry.ronaldo.siufascore.presentation.news.screen.NewsScreen
import com.jerry.ronaldo.siufascore.utils.NavigationTransitionResolver
import com.jerry.ronaldo.siufascore.utils.animComposable
import kotlinx.serialization.Serializable

@Serializable
data object NewsRoute:AppRoute

fun NavGraphBuilder.newsScreen(
    transitionResolver: NavigationTransitionResolver,
    onNewsClick: (String) -> Unit
) {
    animComposable<NewsRoute>(transitionResolver) {
        NewsScreen(
            onNewsClick = onNewsClick
        )
    }
}

fun NavController.navigateToNews(
    navOptions: NavOptions? = null
) {
    navigate(route = NewsRoute, navOptions)
}

@Serializable
data class DetailNewsRoute(val uri: String)

fun NavController.navigateToDetailNews(
    uri: String,
    navOptions: NavOptions? = null
) {
    navigate(route = DetailNewsRoute(uri), navOptions)
}

fun NavGraphBuilder.detailNewsScreen(
    transitionResolver: NavigationTransitionResolver,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    animComposable<DetailNewsRoute>(
        transitionResolver
    ) { route ->
        //Màn hình detailNews
        NewsDetailScreen(
            onBackClick = onBackClick,
            onShareClick = onShareClick,
            newsViewModel = hiltViewModel<NewsDetailViewModel, NewsDetailViewModel.Factory>(
                key = route.uri
            ) { factory ->
                factory.create(conceptUri = route.uri)
            }
        )

    }
}