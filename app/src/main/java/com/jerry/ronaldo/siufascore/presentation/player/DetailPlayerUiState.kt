package com.jerry.ronaldo.siufascore.presentation.player

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.PlayerSeasonStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerStatistics
import com.jerry.ronaldo.siufascore.utils.mapStats
import java.util.Locale

data class DetailPlayerUiState(
    val isStatLoading: Boolean = false,
    val statError: String? = null,
    val isOverviewLoading: Boolean = false,
    val overviewError: String? = null,
    val playerStat: PlayerStatistics? = null,
    val playerOverview: List<TimelineItem>? = emptyList(),
    val selectedTab: PlayerDetailTab = PlayerDetailTab.OVERVIEW,
    val availableSeasons: List<Int> = emptyList(),
    val isFavoritePlayer: Boolean = false,
    val isAddingFavorite: Boolean = false,
    val currentSeason: Int = 2024,
    val hasData: Boolean = false,
) : ViewState {

    // Loading states
    val isLoading: Boolean get() = isStatLoading || isOverviewLoading
    val showError: Boolean get() = (statError != null || overviewError != null) && !isLoading
    val showContent: Boolean get() = hasData && !isLoading
    val showLoading: Boolean get() = isLoading && !hasData
    val showRetry: Boolean get() = showError && !isLoading

    // Error handling
    val errorMessage: String? get() = statError ?: overviewError
    val hasOverviewData: Boolean get() = playerOverview?.isNotEmpty() == true
    val hasStatData: Boolean get() = playerStat != null

    // Player-specific computed properties
    val listPlayerStat: List<PlayerSeasonStats> get() = playerStat?.statistics ?: emptyList()
    val stats = listPlayerStat.groupBy { it.league }.mapValues { (_, playerStats) ->
        playerStats.mapStats()
    }

    // Player basic info
    val playerName: String get() = playerStat?.player?.displayName ?: "Đang tải..."
    val playerPhoto: String get() = playerStat?.player?.photoUrl ?: ""
    val teamName: String get() = playerStat?.currentSeasonStats?.team?.name ?: "Đang tải..."
    val teamLogo: String get() = playerStat?.currentSeasonStats?.team?.logo ?: ""
    val position: String get() = playerStat?.currentSeasonStats?.position ?: "Không rõ"
    val nationality: String get() = playerStat?.player?.nationality ?: "Không rõ"
    val age: Int get() = playerStat?.player?.age ?: 0
    val number: Int get() = playerStat?.player?.number ?: 0
    val weight: String get() = playerStat?.player?.weight ?: "Không rõ"

    // Performance metrics
    val totalGoals: Int get() = playerStat?.totalGoals ?: 0
    val totalAssists: Int get() = playerStat?.totalAssists ?: 0
    val totalAppearances: Int get() = playerStat?.totalAppearances ?: 0
    val totalSubstitutes: Int get() = playerStat?.totalSubs ?: 0

    val rating: String get() = playerStat?.currentSeasonStats?.rating?.let {
        String.format(Locale.US, "%.1f", it)
    } ?: "N/A"

    // UI helpers
    val followButtonText: String get() = if (isFavoritePlayer) "Đang theo dõi" else "Theo dõi"
    val canChangeSeason: Boolean get() = hasData && !isStatLoading


}

enum class PlayerDetailTab(
    val title: String
) {
    OVERVIEW("Tổng quan"),
    STATS("Thống kê");

    companion object {
        fun fromTitle(title: String): PlayerDetailTab? {
            return entries.find { it.title == title }
        }
    }
}

sealed interface DetailPlayerIntent : Intent {
    data object LoadPlayerData : DetailPlayerIntent
    data object RefreshData : DetailPlayerIntent
    data class SelectTab(val tab: PlayerDetailTab) : DetailPlayerIntent
    data object ToggleFollowPlayer : DetailPlayerIntent
    data class ChangeSeason(val season: Int) : DetailPlayerIntent
    data object RetryLoading : DetailPlayerIntent
}

sealed interface DetailPlayerEffect : SingleEvent {
    data object ShowRefreshIndicator : DetailPlayerEffect
    data object ShowFollowSuccess : DetailPlayerEffect
    data object ShowUnfollowSuccess : DetailPlayerEffect
    data class ShowMessage(val message: String) : DetailPlayerEffect
    data class SharePlayer(val playerId: Int, val playerName: String) : DetailPlayerEffect
}