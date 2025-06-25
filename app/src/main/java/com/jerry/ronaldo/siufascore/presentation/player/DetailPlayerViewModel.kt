package com.jerry.ronaldo.siufascore.presentation.player

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.model.PlayerOverview
import com.jerry.ronaldo.siufascore.domain.model.PlayerTeam
import com.jerry.ronaldo.siufascore.domain.model.PlayerTrophy
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetDetailPlayerUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetPlayerOverviewUseCase
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.extractYear
import com.jerry.ronaldo.siufascore.utils.getCurrentSeason
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = DetailPlayerViewModel.Factory::class)
class DetailPlayerViewModel @AssistedInject constructor(
    @Assisted private val playerId: Int,
    private val getDetailPlayerUseCase: GetDetailPlayerUseCase,
    private val getPlayerOverviewUseCase: GetPlayerOverviewUseCase
) :
    BaseViewModel<DetailPlayerIntent, DetailPlayerUiState, DetailPlayerEffect>() {
    private val _selectedTab = MutableStateFlow(PlayerDetailTab.OVERVIEW)
    private val _refreshTrigger = MutableStateFlow(Unit)
    private val _currentSeason = MutableStateFlow(getCurrentSeason())
    private val _availableSeasons = MutableStateFlow(getAvailableSeasons())
    private val _isFollowing = MutableStateFlow(false)
    private val _playerId = MutableStateFlow<Int>(playerId)


    @OptIn(ExperimentalCoroutinesApi::class)
    private val playerStats = combine(
        _refreshTrigger,
        _currentSeason,
        _playerId
    ) { _, season, playerId ->
        Pair(
            season,
            playerId
        )
    }.distinctUntilChanged().flatMapLatest { (season, playerId) ->
        getDetailPlayerUseCase(playerId, season)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.Loading)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val playerOverview = combine(
        _refreshTrigger,
        _playerId
    ) { _, playerId ->
        playerId
    }.distinctUntilChanged().flatMapLatest {
        getPlayerOverviewUseCase(it)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resource.Loading)

    private val basicState = combine(
        _selectedTab,
        _isFollowing
    ) { selectedTab, isFollowing ->
        Pair(selectedTab, isFollowing)
    }

    private val playerStatsState = playerStats.map { statsResult ->
        val statsList = if (statsResult is Resource.Success) statsResult.data else null
        val teamsError =
            if (statsResult is Resource.Error) statsResult.exception.message else null
        val isLoadingTeams = statsResult is Resource.Loading
        Triple(statsList, isLoadingTeams, teamsError)
    }
    private val playerOverviewState = playerOverview.map { overviewResult ->
        val overview = if (overviewResult is Resource.Success) overviewResult.data else null
        val overviewError =
            if (overviewResult is Resource.Error) overviewResult.exception.message else null
        val isLoadingOverview = overviewResult is Resource.Loading
        Triple(overview, isLoadingOverview, overviewError)
    }

    override val uiState: StateFlow<DetailPlayerUiState>
        get() = combine(
            basicState,
            playerStatsState,
            playerOverviewState,
            _currentSeason,
            _availableSeasons
        ) { basic, stats, overview, season, availableSeasons ->
            val (selectedTab, isFollowing) = basic
            val (playerStats, isStatLoading, statError) = stats
            val (playerOverview, isOverviewLoading, overviewError) = overview
            DetailPlayerUiState(
                isStatLoading = isStatLoading,
                statError = statError,
                playerStat = playerStats,
                selectedTab = selectedTab,
                isFollowing = isFollowing,
                currentSeason = season,
                availableSeasons = availableSeasons,
                hasData = playerStats != null && playerOverview != null,
                isOverviewLoading = isOverviewLoading,
                overviewError = overviewError,
                playerOverview = playerOverview?.let {
                    createTimelineItems(it)
                },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailPlayerUiState(isStatLoading = true)
        )

    override suspend fun processIntent(intent: DetailPlayerIntent) {
        when (intent) {
            is DetailPlayerIntent.ChangeSeason -> {
                changeSeason(intent.season)
            }

            DetailPlayerIntent.LoadPlayerData -> {

            }

            DetailPlayerIntent.RefreshData -> {

            }

            DetailPlayerIntent.RetryLoading -> {

            }

            is DetailPlayerIntent.SelectTab -> {
                selectedTab(intent.tab)
            }

            DetailPlayerIntent.ToggleFollow -> {

            }
        }
    }

    private fun refreshPlayerData() {
        viewModelScope.launch {
            sendEvent(DetailPlayerEffect.ShowRefreshIndicator)
            delay(300)
            _refreshTrigger.value = Unit
        }
    }

    private fun selectedTab(tab: PlayerDetailTab) {
        _selectedTab.value = tab
    }

    private fun toggleFollow() {
        val newFollowState = !_isFollowing.value
        _isFollowing.value = newFollowState

        viewModelScope.launch {
            sendEvent(
                if (newFollowState) {
                    DetailPlayerEffect.ShowFollowSuccess
                } else {
                    DetailPlayerEffect.ShowUnfollowSuccess
                }
            )
        }
    }

    private fun changeSeason(season: Int) {
        if (season != _currentSeason.value && season > 2000) {
            _currentSeason.value = season

            viewModelScope.launch {
                sendEvent(DetailPlayerEffect.ShowMessage("Đang tải dữ liệu..."))
            }
        }
    }


    private fun getAvailableSeasons(): List<Int> {
        val currentYear = getCurrentSeason()
        return (currentYear downTo 2000).toList()
    }

    private fun createTimelineItems(
        playerOverview: PlayerOverview
    ): List<TimelineItem> {
        val items = mutableListOf<TimelineItem>()
        val trophiesBySeason =
            playerOverview.trophies.groupBy { it.season.extractYear().toString() }
        val teamsBySeasons = playerOverview.teams.flatMap { team ->
            team.seasons.map { season -> season.toString() to team }
        }.groupBy { it.first }

        val allSeasons = (trophiesBySeason.keys + teamsBySeasons.keys).distinct()
            .sortedByDescending { it.toIntOrNull() ?: 0 }

        allSeasons.forEach { season ->
            val trophies = trophiesBySeason[season] ?: emptyList()
            val teamsPairs = teamsBySeasons[season] ?: emptyList()
            val teams = teamsPairs.map { it.second }.distinctBy { it.teamId }
            items.add(TimelineItem(season, trophies, teams))
        }
        return items
    }

    @AssistedFactory
    interface Factory {
        fun create(playerId: Int): DetailPlayerViewModel
    }

}

data class TimelineItem(
    val season: String,
    val trophies: List<PlayerTrophy>,
    val teams: List<PlayerTeam>
)