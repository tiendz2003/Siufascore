package com.jerry.ronaldo.siufascore.presentation.favorite

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.presentation.ui.TypeFilterChips
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

@Composable
fun FavoriteScreen(
    modifier: Modifier = Modifier,
    onTeamClick:(Int,Int) ->Unit,
    onPlayerClick:(Int) ->Unit,
    favoriteViewModel: FavoriteViewModel = hiltViewModel()
) {
    val uiState by favoriteViewModel.uiState.collectAsStateWithLifecycle()
    Timber.tag("FavoriteScreen").d("FavoriteTeam: ${uiState}")
    LaunchedEffect(Unit) {
        favoriteViewModel.singleEvent.collectLatest { event->
            when(event){
                is FavoriteEvent.NavigateToTeamDetail -> {
                    onTeamClick(event.teamId,event.leagueId)
                }
                is FavoriteEvent.ShowMessage -> {}
                FavoriteEvent.ShowNoFavoriteTeamsMessage -> {

                }

                is FavoriteEvent.NavigateToPlayerDetail -> {
                    onPlayerClick(event.playerId)
                }
            }
        }
    }
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        val hasResult by remember { mutableStateOf(true) }
        TypeFilterChips(
            items = FavoriteType.entries,
            selectedType = uiState.selectedFavoriteType,
            onTypeSelected = { type ->
                favoriteViewModel.sendIntent(FavoriteIntent.SelectFavoriteType(type))
            },
            icon = { type ->
                type.icon
            },
        )
        when {
            hasResult -> {
                when (uiState.selectedFavoriteType) {
                    FavoriteType.TEAMS -> {
                        FavoriteTeamScreen(
                            uiState = uiState,
                            onLeagueSelected = { league ->
                                favoriteViewModel.sendIntent(FavoriteIntent.SelectLeague(league))
                            },
                            onNavigateToTeamDetail = { teamId, leagueId ->
                                favoriteViewModel.sendIntent(
                                    FavoriteIntent.NavigateToTeamDetail(
                                        teamId,
                                        leagueId
                                    )
                                )
                            },
                            onRemoveTeam = { teamId ->
                                favoriteViewModel.sendIntent(
                                    FavoriteIntent.RemoveFavoriteTeam(
                                        teamId
                                    )
                                )
                            },
                            onNotificationToggle = { teamId ->
                                favoriteViewModel.sendIntent(
                                    FavoriteIntent.ToggleNotification(
                                        teamId
                                    )
                                )
                            },
                        )
                    }
                    FavoriteType.PLAYERS -> {
                        FavoritePlayersScreen(
                            uiState = uiState,
                            onPlayerClick = {playerId->
                                favoriteViewModel.sendIntent(
                                    FavoriteIntent.NavigateToPlayerDetail(playerId)
                                )
                            }
                        )
                    }

                    FavoriteType.MATCHES -> {

                    }
                }
            }
        }
    }
}

@Composable
fun Loading(
    color: Color = Purple,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = color,
            strokeWidth = 4.dp
        )
    }
}