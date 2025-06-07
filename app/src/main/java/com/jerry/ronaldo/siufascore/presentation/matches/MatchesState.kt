package com.jerry.ronaldo.siufascore.presentation.matches

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.presentation.mapper.TeamStandingItem

data class MatchesState(
    val matches: List<Match> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val competitionId: String? = null,
    val competionInfo: CompetitionLeague? = null,
    val currentMatchday: Int? = null,
    val standingItem: List<TeamStandingItem> = emptyList(),
    val liveMatches: List<Match> = emptyList(),
    val availableMatchday: List<Int> = emptyList(),
) : ViewState

sealed class MatchesIntent : Intent {
    data class NavigateToDetailMatch(val matchId: Int) : MatchesIntent()
    data class LoadMatchesByLeague(val competitionId: String) : MatchesIntent()
    data class SetMatchday(val matchDay: Int) : MatchesIntent()
    data class SetCompetition(val competitionId: String) : MatchesIntent()
    data object RefreshData : MatchesIntent()
}

sealed class MatchesEffect : SingleEvent {
    data class ShowError(val error: String) : MatchesEffect()
    data class NavigateToDetailMatch(val matchId: Int) : MatchesEffect()
}