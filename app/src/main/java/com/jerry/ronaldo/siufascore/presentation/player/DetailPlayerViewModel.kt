package com.jerry.ronaldo.siufascore.presentation.player

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.data.model.FavoritePlayer
import com.jerry.ronaldo.siufascore.domain.model.PlayerOverview
import com.jerry.ronaldo.siufascore.domain.model.PlayerTeam
import com.jerry.ronaldo.siufascore.domain.model.PlayerTrophy
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.AddFavoritePlayerUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.ObserveFavoritePlayersUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.RemoveFavoritePlayerUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetDetailPlayerUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetPlayerOverviewUseCase
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.extractYear
import com.jerry.ronaldo.siufascore.utils.getCurrentSeason
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

@HiltViewModel(assistedFactory = DetailPlayerViewModel.Factory::class)
class DetailPlayerViewModel @AssistedInject constructor(
    @Assisted private val playerId: Int,
    private val getDetailPlayerUseCase: GetDetailPlayerUseCase,
    private val getPlayerOverviewUseCase: GetPlayerOverviewUseCase,
    private val addFavoritePlayer: AddFavoritePlayerUseCase,
    private val removeFavoritePlayer: RemoveFavoritePlayerUseCase,
    private val observeFavoritePlayerUseCase: ObserveFavoritePlayersUseCase,
) :
    BaseViewModel<DetailPlayerIntent, DetailPlayerUiState, DetailPlayerEffect>() {


    private val _uiState = MutableStateFlow(
        DetailPlayerUiState(
            isStatLoading = true,
            currentSeason = getCurrentSeason(),
            availableSeasons = getAvailableSeasons()
        )
    )
    override val uiState: StateFlow<DetailPlayerUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
        observeFavoriteStatus()
    }

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
                selectTab(intent.tab)
            }

            DetailPlayerIntent.ToggleFollowPlayer -> {
                toggleFollow()
            }
        }
    }

    private fun updateState(newState: (DetailPlayerUiState) -> DetailPlayerUiState) {
        _uiState.update { state ->
            newState(state)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadPlayerData()
            loadPlayerOverview()
        }
    }

    private fun loadPlayerData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isStatLoading = true,
                statError = null
            )

            try {
                getDetailPlayerUseCase(playerId, _uiState.value.currentSeason).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            Timber.d("Player data loaded successfully: ${result.data.player.displayName}")
                            _uiState.value = _uiState.value.copy(
                                isStatLoading = false,
                                playerStat = result.data,
                                statError = null,
                                hasData = _uiState.value.playerOverview != null
                            )
                        }

                        is Resource.Error -> {
                            Timber.e("Failed to load player data: ${result.exception.message}")
                            _uiState.value = _uiState.value.copy(
                                isStatLoading = false,
                                statError = result.exception.message
                            )
                        }

                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isStatLoading = true)
                        }
                    }
                }

            } catch (e: Exception) {
                Timber.e(e, "Exception while loading player data")
                _uiState.value = _uiState.value.copy(
                    isStatLoading = false,
                    statError = e.message ?: "Unknown error"
                )
            }
        }
    }

    private fun loadPlayerOverview() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isOverviewLoading = true,
                overviewError = null
            )

            try {
                getPlayerOverviewUseCase(playerId).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            val timelineItems = result.data.let { createTimelineItems(it) }
                            _uiState.value = _uiState.value.copy(
                                isOverviewLoading = false,
                                playerOverview = timelineItems,
                                overviewError = null,
                                hasData = _uiState.value.playerStat != null
                            )
                        }

                        is Resource.Error -> {
                            _uiState.value = _uiState.value.copy(
                                isOverviewLoading = false,
                                overviewError = result.exception.message
                            )
                        }

                        is Resource.Loading -> {
                            _uiState.value = _uiState.value.copy(isOverviewLoading = true)
                        }
                    }
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isOverviewLoading = false,
                    overviewError = e.message ?: "Unknown error"
                )
            }
        }
    }

    private fun observeFavoriteStatus() {
        viewModelScope.launch {
            observeFavoritePlayerUseCase().collect { favoritePlayersResult ->
                when (favoritePlayersResult) {
                    is Resource.Success -> {
                        val isFavorite = favoritePlayersResult.data.any {
                            it.playerId == playerId.toString()
                        }

                        _uiState.value = _uiState.value.copy(isFavoritePlayer = isFavorite)
                        Timber.d("Favorite status updated: $isFavorite")
                    }

                    is Resource.Error -> {
                        Timber.e("Error observing favorite status: ${favoritePlayersResult.exception.message}")
                        _uiState.value = _uiState.value.copy(isFavoritePlayer = false)
                    }

                    is Resource.Loading -> {
                        // Keep current state during loading
                    }
                }
            }
        }
    }

    private fun selectTab(tab: PlayerDetailTab) {
        updateState {
            it.copy(selectedTab = tab)
        }
    }

    private suspend fun toggleFollow() {

        val currentState = _uiState.value

        // Detailed logging for debugging
        Timber.d("=== ToggleFollow Debug ===")
        Timber.d("Current isAddingFavorite: ${currentState.isAddingFavorite}")
        Timber.d("Player data is null: ${currentState.playerStat == null}")
        Timber.d("Current isFavoritePlayer: ${currentState.isFavoritePlayer}")

        if (currentState.playerStat == null) {
            Timber.w("Cannot toggle follow - player data is null")
            sendEvent(DetailPlayerEffect.ShowMessage("Dữ liệu chưa được tải, vui lòng thử lại"))
            return
        }

        if (currentState.isAddingFavorite) {
            Timber.d("Already processing favorite toggle, ignoring")
            return
        }

        val playerData = currentState.playerStat
        Timber.d("Processing toggle for player: ${playerData.player.displayName}")

        // Set loading state
        _uiState.value = _uiState.value.copy(isAddingFavorite = true)

        try {
            val result = if (currentState.isFavoritePlayer) {
                Timber.d("Removing from favorites")
                removeFavoritePlayer(playerData.player.id.toString())
            } else {
                Timber.d("Adding to favorites")
                addFavoritePlayer(
                    player = FavoritePlayer(
                        playerId = playerData.player.id.toString(),
                        playerName = playerData.player.displayName,
                        playerPhoto = playerData.player.photoUrl,
                        playerNationality = playerData.player.nationality ?: "",
                        playerPosition = playerData.currentSeasonStats?.position ?: ""
                    )
                )
            }

            result.onSuccess {
                Timber.d("Toggle favorite operation completed successfully")
                val successMessage = if (currentState.isFavoritePlayer) {
                    "Đã bỏ theo dõi ${playerData.player.displayName}"
                } else {
                    "Đã theo dõi ${playerData.player.displayName}"
                }
                sendEvent(DetailPlayerEffect.ShowMessage(successMessage))
            }.onFailure { error ->
                Timber.e(error, "Failed to toggle favorite")
                sendEvent(DetailPlayerEffect.ShowMessage("Lỗi: ${error.message ?: "Không rõ"}"))
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during toggle favorite")
            sendEvent(DetailPlayerEffect.ShowMessage("Lỗi: ${e.message ?: "Không rõ"}"))
        } finally {
            // Reset loading state
            _uiState.value = _uiState.value.copy(isAddingFavorite = false)
        }

    }

    private fun changeSeason(season: Int) {
        if (season != _uiState.value.currentSeason && season > 2000) {
            _uiState.value = _uiState.value.copy(currentSeason = season)
            loadPlayerData() // Reload data for new season

            viewModelScope.launch {
                sendEvent(DetailPlayerEffect.ShowMessage("Đang tải dữ liệu mùa giải $season..."))
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