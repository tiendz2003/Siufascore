package com.jerry.ronaldo.siufascore.presentation.matches.screen.detail

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.DetailMatch
import com.jerry.ronaldo.siufascore.domain.model.MatchEvent
import com.jerry.ronaldo.siufascore.domain.model.MatchLineups
import com.jerry.ronaldo.siufascore.domain.model.MatchStatistics
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.PlayerMatchStats
import com.jerry.ronaldo.siufascore.domain.model.StatComparison
import com.jerry.ronaldo.siufascore.domain.model.TeamPlayerStats

data class DetailMatchState(
    val isLoading: Boolean = true,
    val fixtureDetail: DetailMatch? = null,
    val error: String? = null,
    val selectedTab: MatchDetailTab = MatchDetailTab.OVERVIEW,
    val homeTeamId: Int = 0,
    val awayTeamId: Int = 0,
    val h2hInfo: List<Match>?= null,
    val h2hLoading: Boolean = false,
    val h2hError: String? = null
) : ViewState {
    // Direct access to UI-ready domain models
    val match: Match? get() = fixtureDetail?.match
    val events: List<MatchEvent> get() = fixtureDetail?.events ?: emptyList()
    val lineups: MatchLineups? get() = fixtureDetail?.lineups
    val statistics: MatchStatistics? get() = fixtureDetail?.statistics
    val playerStats: List<TeamPlayerStats> get() = fixtureDetail?.playerStatistics ?: emptyList()

    // Event categories (using domain computed properties)
    val goalEvents: List<MatchEvent> get() = fixtureDetail?.goalEvents ?: emptyList()
    val timelineEvents: List<MatchEvent> get() = fixtureDetail?.timelineEvents ?: emptyList()

    // Team-specific data
    val homeTeamStats: TeamPlayerStats? get() = playerStats.find { it.team.id == homeTeamId }
    val awayTeamStats: TeamPlayerStats? get() = playerStats.find { it.team.id == awayTeamId }

    val homeTeamFormation: String? get() = lineups?.awayTeam?.formation
    val homeTeamLineup: List<PlayerMatchStats>
        get() = playerStats.find { it.team.id == homeTeamId }?.startingXI ?: emptyList()
    val awayTeamLineup: List<PlayerMatchStats>
        get() = playerStats.find { it.team.id == awayTeamId }?.startingXI ?: emptyList()
    val awayTeamFormation: String? get() = lineups?.awayTeam?.formation
    val statisticsComparison: List<StatComparison>
        get() = statistics?.getComparisonStats() ?: emptyList()

    // Top performers (using domain computed properties)
    val topScorers: List<PlayerMatchStats> get() = fixtureDetail?.topScorers ?: emptyList()
    val topRatedPlayers: List<PlayerMatchStats>
        get() = fixtureDetail?.topRatedPlayers ?: emptyList()
}

sealed class DetailMatchIntent : Intent {
    data object LoadStatistic : DetailMatchIntent()
    data object LoadH2H : DetailMatchIntent()
    data class SelectTab(val tab: MatchDetailTab) : DetailMatchIntent()
}

sealed class DetailMatchEvent : SingleEvent {

}

enum class MatchDetailTab(val title: String) {
    OVERVIEW("Tổng quan"),
    EVENTS("Sự kiện"),
    LINEUPS("Đội hình"),
    H2H("H2H")
}