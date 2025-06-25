package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.jerry.ronaldo.siufascore.presentation.news.NewsDetailViewModel
import com.jerry.ronaldo.siufascore.presentation.news.screen.NewsDetailScreen
import com.jerry.ronaldo.siufascore.presentation.news.screen.NewsScreen
import kotlinx.serialization.Serializable

@Serializable
data object NewsRoute

fun NavGraphBuilder.newsScreen(
    onNewsClick: (String) -> Unit
) {
    composable<NewsRoute> {
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
data class NewsNavigationRoute(val uri: String)

fun NavController.navigateToDetailNews(
    uri: String,
    navOptions: NavOptions? = null
) {
    navigate(route = NewsNavigationRoute(uri), navOptions)
}

fun NavGraphBuilder.detailNewsScreen(
    onBackClick: () -> Unit,
    onShareClick: () -> Unit
) {
    composable<NewsNavigationRoute> { entry ->
        val newsUri = entry.toRoute<NewsNavigationRoute>().uri
        //Màn hình detailNews
        NewsDetailScreen(
            onBackClick = onBackClick,
            onShareClick = onShareClick,
            newsViewModel = hiltViewModel<NewsDetailViewModel, NewsDetailViewModel.Factory>(
                key = newsUri
            ) { factory ->
                factory.create(conceptUri = newsUri)
            }
        )

    }
}