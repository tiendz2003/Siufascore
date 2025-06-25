package com.jerry.ronaldo.siufascore.presentation.search

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.PlayerSearch
import com.jerry.ronaldo.siufascore.domain.model.TeamSearch

data class SearchState(
    val selectedSearchType: SearchType = SearchType.DEFAULT,
    val players: List<PlayerSearch> = emptyList(),
    val isLoadingPlayers: Boolean = false,
    val playersError: String? = null,
    val currentPage: Int = 1,
    val totalPages: Int = 0,
    val hasNextPage: Boolean = false,
    val hasPreviousPage: Boolean = false,
    val totalResults: Int = 0,
    val isLoadingMorePlayers: Boolean = false,
    val isLoadingTeams: Boolean = false,
    val teams: List<TeamSearch> = emptyList(),
    val teamsError: String? = null,
    val query: String = "",
    val hasSearched: Boolean = false,
    val recentSearches: List<String> = emptyList()
) : ViewState {
    val isLoading: Boolean
        get() = when (selectedSearchType) {
            SearchType.TEAMS -> isLoadingTeams
            SearchType.PLAYERS -> isLoadingPlayers
        }
    val error: String?
        get() = when (selectedSearchType) {
            SearchType.TEAMS -> teamsError
            SearchType.PLAYERS -> playersError
        }
    val hasResults: Boolean
        get() = when (selectedSearchType) {
            SearchType.TEAMS -> teams.isNotEmpty()
            SearchType.PLAYERS -> players.isNotEmpty()
        }
    val showEmptyState: Boolean get() = hasSearched && !hasResults && !isLoading && error == null
    val showPagination: Boolean
        get() = selectedSearchType == SearchType.PLAYERS &&
                players.isNotEmpty() && totalPages > 1
    val resultsInfo: String
        get() = when (selectedSearchType) {
            SearchType.TEAMS -> if (teams.isNotEmpty()) "Tìm thấy ${teams.size} câu lạc bộ" else ""
            SearchType.PLAYERS -> if (totalResults > 0) "Tìm thấy $totalResults cầu thủ" else ""
        }
    val pageInfo: String
        get() = if (selectedSearchType == SearchType.PLAYERS && totalPages > 0) {
            "$currentPage / $totalPages"
        } else ""
    val canLoadMorePlayers: Boolean get() = selectedSearchType == SearchType.PLAYERS && !isLoadingMorePlayers && hasNextPage && !isLoadingPlayers

    val showResults: Boolean get() = teams.isNotEmpty()
    val canSearch: Boolean get() = query.length >= 3
    val showRecentSearches: Boolean get() = !hasSearched && recentSearches.isNotEmpty() && query.isEmpty()
}

sealed class SearchIntent : Intent {
    data class ChangedSearchType(val type: SearchType) : SearchIntent()
    data class Search(val query: String) : SearchIntent()
    data class UpdateQuery(val query: String) : SearchIntent()
    data class LoadMorePlayers(val query: String) : SearchIntent()
    data class LoadPlayersPage(val query: String, val page: Int) : SearchIntent()
    data object ClearSearch : SearchIntent()
    data object ClearError : SearchIntent()
    data class SelectRecentSearch(val query: String) : SearchIntent()
    data class RemoveRecentSearch(val query: String) : SearchIntent()
    data class NavigateToTeamDetail(val teamId: Int,val leagueId:Int) : SearchIntent()
    data class NavigateToPlayerDetail(val playerId: Int) : SearchIntent()
}

sealed class SearchEvent : SingleEvent {
    data class NavigateToTeamDetail(val teamId: Int,val leagueId:Int) : SearchEvent()
    data class NavigateToPlayerDetail(val playerId: Int) : SearchEvent()
    data class ShowSnackbar(val message: String) : SearchEvent()
}