package com.jerry.ronaldo.siufascore.presentation.news.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.jerry.ronaldo.siufascore.presentation.news.NewsIntent
import com.jerry.ronaldo.siufascore.presentation.news.NewsViewModel
import com.jerry.ronaldo.siufascore.presentation.news.item.NewsItemCard
import com.jerry.ronaldo.siufascore.presentation.news.item.NewsItemSmallCard
import com.jerry.ronaldo.siufascore.presentation.news.item.ShimmerNewsItemCard
import com.jerry.ronaldo.siufascore.presentation.news.item.ShimmerNewsItemSmall
import com.jerry.ronaldo.siufascore.presentation.ui.EmptyScreen
import com.jerry.ronaldo.siufascore.presentation.ui.ErrorItem
import com.jerry.ronaldo.siufascore.presentation.ui.FilterChipItem

@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    onNewsClick: (String) -> Unit,
    newsViewModel: NewsViewModel = hiltViewModel()
) {
    val newsPagingData = newsViewModel.newsPagingData.collectAsLazyPagingItems()
    val uiState by newsViewModel.uiState.collectAsStateWithLifecycle()

    val availableFilters = newsViewModel.getAvailableFilters()
    when {
        newsPagingData.loadState.refresh is LoadState.Loading -> {
            ShimmerNewsScreen(modifier) // Hiển thị shimmer khi đang tải
        }
        newsPagingData.loadState.refresh is LoadState.Error -> {
            val error = newsPagingData.loadState.refresh as LoadState.Error
            ErrorScreen(
                message = error.error.localizedMessage ?: "Error loading news",
                onRetry = { newsPagingData.retry() }
            )
        }

        newsPagingData.itemCount == 0 && newsPagingData.loadState.refresh is LoadState.NotLoading -> {
            EmptyScreen()
        }

        else -> {
            LazyColumn(
                modifier = modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item("filter_chips") {
                    FilterChipItem(
                        options = availableFilters,
                        selectedOption = uiState.selectedFilter,
                        onSelectedOptionChange = { filterName ->
                            newsViewModel.sendIntent(NewsIntent.FilterNews(filterName))
                        },
                    )
                }
                item(key = "hotnews_section") {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(
                            count = newsPagingData.itemCount,
                            key = { index -> "row-$index" }
                        ) { index ->
                            val article = newsPagingData[index]
                            article?.let {
                                NewsItemCard(
                                    article = article,
                                    onItemClick = { onNewsClick(article.uri) },
                                    onShareNewClick = { /* Logic chia sẻ */ },
                                )
                            }
                        }
                    }
                }
                item("title_section") {
                    Text(
                        text = "Tin nổi bật",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
                items(
                    count = newsPagingData.itemCount,
                    key = { index ->
                        val article = newsPagingData.peek(index)
                        article?.uri ?: "item-$index"
                    }
                ) { index ->
                    val article = newsPagingData[index]
                    article?.let {
                        NewsItemSmallCard(
                            article = article,
                            onClick = { onNewsClick(article.uri) },
                        )
                    }
                }
                if (newsPagingData.loadState.append is LoadState.Loading) {
                    item("loading_more") {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
                if (newsPagingData.loadState.append is LoadState.Error) {
                    item("error_more") {
                        val error = newsPagingData.loadState.append as LoadState.Error
                        ErrorItem(
                            message = error.error.localizedMessage ?: "Error loading more news",
                            onRetry = { newsPagingData.retry() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerNewsScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(2) { // Hiển thị 3 placeholder cho NewsItemCard
                    ShimmerNewsItemCard()
                }
            }
        }
        item {
            Text(
                text = "Tin nổi bật",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(5) { // Hiển thị 5 placeholder cho NewsItemSmallCard
            ShimmerNewsItemSmall()
        }
    }
}

@Composable
fun ErrorScreen(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = message, color = MaterialTheme.colorScheme.error)
        Button(onClick = onRetry, modifier = Modifier.padding(top = 8.dp)) {
            Text("Retry")
        }
    }
}