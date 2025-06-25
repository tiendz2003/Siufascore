package com.jerry.ronaldo.siufascore.presentation.team

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.data.model.ResponseTeamStatistics
import com.jerry.ronaldo.siufascore.presentation.player.PlayerDetailTab

data class DetailTeamUiState(
    val teamStatistic: ResponseTeamStatistics? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isFavoriteTeam: Boolean = false,
    val isAddingFavorite: Boolean = false,
    val selectedTab: PlayerDetailTab = PlayerDetailTab.OVERVIEW
) : ViewState {

}

sealed class DetailTeamIntent : Intent {
    data class SelectTab(val tab: PlayerDetailTab) : DetailTeamIntent()
    data object OnToggleFavoriteTeam : DetailTeamIntent()
}

sealed interface DetailTeamEvent : SingleEvent {
    data class NavigateToDetailPlayer(val playerId: Int) : DetailTeamEvent
    data object AddFavoriteTeamSuccess:DetailTeamEvent
    data object ErrorFavorite:DetailTeamEvent
    data object RemoveFavoriteTeamSuccess:DetailTeamEvent
    data object OnNavigateBack : DetailTeamEvent
}