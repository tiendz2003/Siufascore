package com.jerry.ronaldo.siufascore.presentation.search

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.model.PlayerSearch
import com.jerry.ronaldo.siufascore.domain.model.TeamSearch
import com.jerry.ronaldo.siufascore.domain.usecase.search.SearchPlayersUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.search.SearchTeamsUseCase
import com.jerry.ronaldo.siufascore.utils.PaginatedResult
import com.jerry.ronaldo.siufascore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTeamsUseCase: SearchTeamsUseCase,
    private val searchPlayersUseCase: SearchPlayersUseCase,
    private val savedStateHandle: SavedStateHandle
) : BaseViewModel<SearchIntent, SearchState, SearchEvent>() {
    companion object {
        private const val KEY_EXPANDED_IDS = "expanded_ids"
    }

    private val _expandedClubIds = savedStateHandle.getStateFlow(KEY_EXPANDED_IDS, emptySet<Int>())
    val expandedClubIds: StateFlow<Set<Int>> = _expandedClubIds

    private val _selectedSearchType = MutableStateFlow(SearchType.DEFAULT)
    private val _hasSearched = MutableStateFlow(false)
    private val _searchQuery = MutableStateFlow("")

    private val _teamsSearchTrigger = MutableStateFlow<String?>(null)

    private val _players = MutableStateFlow<List<PlayerSearch>>(emptyList())
    private val _playersSearchTrigger = MutableStateFlow<PlayerSearchTrigger?>(null)

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val teamsSearchResults = _teamsSearchTrigger
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            if (!query.isNullOrBlank() && query.length >= 3) {
                searchTeamsUseCase(query.trim())
            } else {
                flowOf<Resource<List<TeamSearch>>?>(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    // Players search flow
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    private val playersSearchResults = _playersSearchTrigger
        .debounce { trigger ->
            if (trigger?.isLoadMore == true || trigger?.page != 1) 0L else 500L
        }
        .distinctUntilChanged()
        .flatMapLatest { trigger ->
            if (trigger != null && trigger.query.length >= 3) {
                searchPlayersUseCase(trigger.query.trim(), trigger.page)
            } else {
                flowOf<Resource<PaginatedResult<PlayerSearch>>?>(null)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null
        )

    private val basicState = combine(
        _selectedSearchType,
        _searchQuery,
        _hasSearched
    ) { selectedType, query, hasSearched ->
        Triple(selectedType, query, hasSearched)
    }
    private val teamsState = teamsSearchResults.map { teamsResults ->
        val teamsList = if (teamsResults is Resource.Success) teamsResults.data else emptyList()
        val teamsError =
            if (teamsResults is Resource.Error) teamsResults.exception.message else null
        val isLoadingTeams = teamsResults is Resource.Loading
        Triple(teamsList, isLoadingTeams, teamsError)
    }

    private val playersState = combine(
        playersSearchResults,
        _players,
        _playersSearchTrigger
    ) { playersResults, accumulatedPlayers, playersSearchTrigger ->
        val playersError =
            if (playersResults is Resource.Error) playersResults.exception.message else null
        val paginatedPlayersResult =
            if (playersResults is Resource.Success) playersResults.data else null
        val isLoadingMorePlayers =
            playersSearchTrigger?.isLoadMore == true && playersResults is Resource.Loading
        val isLoadingPlayers = playersResults is Resource.Loading && !isLoadingMorePlayers
        PlayerStateData(
            players = accumulatedPlayers,
            isLoading = isLoadingPlayers,
            isLoadingMore = isLoadingMorePlayers,
            error = playersError,
            paginatedResult = paginatedPlayersResult
        )
    }

    override val uiState: StateFlow<SearchState>
        get() = combine(
            basicState,
            teamsState,
            playersState
        ) { basic, teams, players ->
            val (selectedType, query, hasSearched) = basic
            val (teamsList, isLoadingTeams, teamsError) = teams

            SearchState(
                selectedSearchType = selectedType,
                query = query,
                hasSearched = hasSearched,
                teams = teamsList,
                isLoadingTeams = isLoadingTeams,
                teamsError = teamsError,
                playersError = players.error,
                players = players.players,
                isLoadingPlayers = players.isLoading,
                isLoadingMorePlayers = players.isLoadingMore,
                currentPage = players.paginatedResult?.currentPage ?: uiState.value.currentPage,
                totalPages = players.paginatedResult?.totalPages ?: uiState.value.totalPages,
                hasNextPage = players.paginatedResult?.hasNextPage ?: uiState.value.hasNextPage,
                totalResults = players.paginatedResult?.totalResults ?: 0,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = SearchState()
        )

    init {
        observeSearchResult()
    }

    fun onClubClicked(clubId: Int) {
        val currentIds = _expandedClubIds.value
        val newIds = if (clubId in currentIds) {
            currentIds - clubId
        } else {
            currentIds + clubId
        }
        savedStateHandle[KEY_EXPANDED_IDS] = newIds
    }

    override suspend fun processIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.Search -> {
                search(intent.query)
            }

            is SearchIntent.UpdateQuery -> {
                updateQuery(intent.query)
            }

            SearchIntent.ClearSearch -> {
                clearSearch()
            }

            SearchIntent.ClearError -> {

            }

            is SearchIntent.SelectRecentSearch -> {

            }

            is SearchIntent.RemoveRecentSearch -> {

            }

            is SearchIntent.NavigateToTeamDetail -> {
                sendEvent(SearchEvent.NavigateToTeamDetail(intent.teamId,intent.leagueId))
            }
            is SearchIntent.NavigateToPlayerDetail -> {
                sendEvent(SearchEvent.NavigateToPlayerDetail(intent.playerId))
            }

            is SearchIntent.ChangedSearchType -> {
                changeSearchType(intent.type)
            }

            is SearchIntent.LoadMorePlayers -> {
                loadMorePlayers(intent.query)
            }

            is SearchIntent.LoadPlayersPage -> {
                loadPlayersPage(intent.query, intent.page)
            }
        }
    }

    private fun observeSearchResult() {
        viewModelScope.launch {
            playersSearchResults.collect { result ->
                when (result) {
                    is Resource.Error -> {

                    }

                    is Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        val trigger = _playersSearchTrigger.value
                        if (trigger?.isLoadMore == true) {
                            _players.value += result.data.data
                        } else {
                            _players.value = result.data.data
                        }
                    }

                    else -> {
                        // Loading or null
                    }
                }
            }
        }
    }

    private fun search(query: String) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.length < 3) {
            viewModelScope.launch {
                sendEvent(SearchEvent.ShowSnackbar("Vui lòng nhập ít nhất 3 ký tự"))
            }
            return
        }
        performSearch(query)
    }

    private fun updateQuery(query: String) {
        _searchQuery.value = query

        if (query.length >= 3) {
            performSearch(query)
        } else if (query.isEmpty()) {
            clearSearchResults()
        }
    }

    private fun clearSearch() {
        _searchQuery.value = ""
        _hasSearched.value = false
        clearSearchResults()
    }

    private fun changeSearchType(searchType: SearchType) {
        val currentType = _selectedSearchType.value
        if (currentType != searchType) {
            _selectedSearchType.value = searchType
            _hasSearched.value = false
            val currentQuery = _searchQuery.value
            if (currentQuery.isNotEmpty() && currentQuery.length >= 3) {
                performSearch(currentQuery)
            }
        }
    }

    private fun performSearch(query: String) {
        val trimmedQuery = query.trim()
        _hasSearched.value = true

        when (_selectedSearchType.value) {
            SearchType.TEAMS -> {
                _teamsSearchTrigger.value = trimmedQuery
            }

            SearchType.PLAYERS -> {
                _players.value = emptyList()
                _playersSearchTrigger.value = PlayerSearchTrigger(trimmedQuery, 1)
            }
        }
    }

    private fun loadMorePlayers(query: String) {
        val currentState = uiState.value
        if (currentState.canLoadMorePlayers) {
            _playersSearchTrigger.value = PlayerSearchTrigger(
                query = query,
                page = currentState.currentPage + 1,
                isLoadMore = true
            )
        }
    }

    private fun loadPlayersPage(query: String, page: Int) {
        _playersSearchTrigger.value = PlayerSearchTrigger(
            query = query,
            page = page,
            isLoadMore = false
        )
    }

    private fun clearSearchResults() {
        _teamsSearchTrigger.value = null
        _playersSearchTrigger.value = null
        _players.value = emptyList()
    }

    private data class PlayerSearchTrigger(
        val query: String,
        val page: Int,
        val isLoadMore: Boolean = false
    )

    private data class PlayerStateData(
        val players: List<PlayerSearch>,
        val isLoading: Boolean,
        val isLoadingMore: Boolean,
        val error: String?,
        val paginatedResult: PaginatedResult<PlayerSearch>?
    )
}