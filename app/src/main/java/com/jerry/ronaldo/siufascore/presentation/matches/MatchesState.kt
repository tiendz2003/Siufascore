package com.jerry.ronaldo.siufascore.presentation.matches

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.model.LeagueStandings
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.presentation.mapper.TeamStandingItem
import com.jerry.ronaldo.siufascore.presentation.mapper.mapToTeamStandingItems

data class MatchesState(
    val matches: List<Match> = emptyList(),
    val isLoading: Boolean = false,
    val isMatchesLoading: Boolean = false,
    val isStandingsLoading: Boolean = false,
    val error: String? = null,
    val matchesError: String? = null,
    val standingError: String? = null,
    val competitionId: Int? = null,
    val competionInfo: CompetitionLeague? = null,
    val currentMatchday: String? = null,
    val standings: List<LeagueStandings> = emptyList(),
    val standingItem: List<TeamStandingItem> = emptyList(),
    val liveMatches: List<Match> = emptyList(),
    val availableMatchday: List<Int> = emptyList(),
) : ViewState {
    val standingItems: List<TeamStandingItem>
        get() = standings.mapToTeamStandingItems() ?: emptyList()
}

sealed class MatchesIntent : Intent {
    data class NavigateToDetailMatch(val matchId: Int) : MatchesIntent()
    data class LoadMatchesByLeague(val competitionId: Int) : MatchesIntent()
    data class SetMatchday(val matchDay: String) : MatchesIntent()
    data class SetCompetition(val competitionId: Int) : MatchesIntent()
    data object RefreshData : MatchesIntent()
}

sealed class MatchesEffect : SingleEvent {
    data class ShowError(val error: String) : MatchesEffect()
    data class NavigateToDetailMatch(val matchId: Int) : MatchesEffect()
}