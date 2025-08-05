package com.jerry.ronaldo.siufascore.presentation.team

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.ronaldo.siufascore.presentation.player.PlayerDetailTab
import com.jerry.ronaldo.siufascore.presentation.player.PlayerHeader
import com.jerry.ronaldo.siufascore.presentation.ui.Purple

@Composable
fun DetailTeamScreen(
    modifier: Modifier = Modifier,
    viewModel: DetailTeamViewModel = hiltViewModel(),
    onPlayerClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTabIndex by remember { mutableIntStateOf(0) }


    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            PlayerHeader(
                playerImage = uiState.teamStatistic?.team?.logo ?: "",
                playerTeamLogo = uiState.teamStatistic?.league?.logo ?: "Không rõ",
                playerName = uiState.teamStatistic?.team?.name ?: "Không rõ",
                playerTeam = uiState.teamStatistic?.league?.name ?: "Không rõ",
                playerPosition = uiState.teamStatistic?.league?.country ?: "Không rõ",
                isFavorite = uiState.isFavoriteTeam,
                onBackClick = onBackClick,
                onToggleFollow = {
                    viewModel.sendIntent(DetailTeamIntent.OnToggleFavoriteTeam)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            )
        }
        item {
            val tabs = listOf(
                PlayerDetailTab.OVERVIEW,
                PlayerDetailTab.STATS
            )
            val selectedTab = tabs.indexOf(uiState.selectedTab).coerceAtLeast(0)

            ScrollableTabRow(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        color = Color.Transparent
                    )
                    .padding(bottom = 16.dp),
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 12.dp,
                divider = {},
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier
                            .tabIndicatorOffset(tabPositions[selectedTabIndex])
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                        height = 3.dp,
                        color = Purple
                    )
                }
            ) {
                tabs.forEachIndexed { index, tabs ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = {
                            selectedTabIndex = index
                            viewModel.sendIntent(DetailTeamIntent.SelectTab(tabs))
                        },
                        text = {
                            Text(
                                tabs.title, style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = 14.sp
                                )
                            )
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = {
                    if (targetState > initialState) {
                        (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> -width } + fadeOut())
                    } else {
                        (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                            slideOutHorizontally { width -> width } + fadeOut())
                    }.using(SizeTransform(clip = false))
                }
            ) { index ->
                when (tabs[index]) {
                    PlayerDetailTab.OVERVIEW -> {
                        uiState.teamStatistic?.let {statistic->
                            TeamStatisticsScreen(
                                statistics = statistic,
                            )
                        }
                    }
                    PlayerDetailTab.STATS -> {

                    }
                }
            }
        }
    }
}