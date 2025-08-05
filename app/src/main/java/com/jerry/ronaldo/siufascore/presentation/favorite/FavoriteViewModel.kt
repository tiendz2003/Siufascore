package com.jerry.ronaldo.siufascore.presentation.favorite

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.GetFavoriteTeamsByLeagueUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.ObserveFavoritePlayersUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.RemoveFavoritePlayerUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.RemoveFavoriteTeamUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.ToggleNotificationUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.TogglePlayerNotificationUseCase
import com.jerry.ronaldo.siufascore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteTeamsByLeagueUseCase: GetFavoriteTeamsByLeagueUseCase,
    private val getFavoritePlayerUseCase: ObserveFavoritePlayersUseCase,
    private val toggleTeamNotificationUseCase: ToggleNotificationUseCase,
    private val removeFavoriteTeamUseCase: RemoveFavoriteTeamUseCase,
    private val togglePlayerNotificationUseCase: TogglePlayerNotificationUseCase,
    private val removeFavoritePlayerUseCase: RemoveFavoritePlayerUseCase
) : BaseViewModel<FavoriteIntent, FavoriteUiState, FavoriteEvent>() {
    private val _selectedLeague = MutableStateFlow(AvailableLeague.DEFAULT)
    private val _selectedTab = MutableStateFlow(FavoriteType.DEFAULT)
    private val _isTogglingNotification = MutableStateFlow(false)
    private val _toggledTeamId = MutableStateFlow<Int?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _favoriteTeamsByLeague: StateFlow<Map<String, List<FavoriteTeam>>> =
        _selectedTab.flatMapLatest { tab ->
            if (tab == FavoriteType.TEAMS) {
                getFavoriteTeamsByLeagueUseCase()
            } else {
                MutableStateFlow(emptyMap())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _favoritePlayer = _selectedTab.flatMapLatest { tab ->
        if (tab == FavoriteType.PLAYERS) {
            Timber.tag("FavoriteViewModel").d("Fetching favorite players")
            getFavoritePlayerUseCase()
        } else {
            MutableStateFlow(Resource.Success(emptyList()))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = Resource.Success(emptyList())
    )
    private val basicState = combine(
        _selectedLeague,
        _isTogglingNotification,
        _toggledTeamId,
    ) { selectedLeague, isTogglingNotification, toggledTeamId ->
        Triple(selectedLeague, isTogglingNotification, toggledTeamId)
    }
    override val uiState: StateFlow<FavoriteUiState>
        get() = combine(
            basicState,
            _favoriteTeamsByLeague,
            _favoritePlayer,
            _selectedTab,
        ) { basic, favoriteTeams, favoritePlayer, selectedTab ->
            Timber.tag("FavoriteViewModel").d("favoriteTeams:${favoritePlayer}")
            val (selectedLeague, isTogglingNotification, toggledTeamId) = basic
            val isLoading = favoriteTeams.isEmpty()
            FavoriteUiState(
                favoriteTeamsByLeague = favoriteTeams,
                selectedLeagueType = selectedLeague,
                isTeamLoading = isLoading,
                isPlayerLoading = favoritePlayer is Resource.Loading,
                selectedFavoriteType = selectedTab,
                favoritePlayers = if (favoritePlayer is Resource.Success) favoritePlayer.data else emptyList(),
                isTogglingNotification = isTogglingNotification,
                toggledTeamId = toggledTeamId
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoriteUiState(isTeamLoading = true)
        )

    override suspend fun processIntent(intent: FavoriteIntent) {
        when (intent) {
            is FavoriteIntent.NavigateToTeamDetail -> {
                sendEvent(FavoriteEvent.NavigateToTeamDetail(intent.teamId, intent.leagueId))
            }

            FavoriteIntent.Refresh -> {

            }

            is FavoriteIntent.RemoveFavoriteTeam -> {
                removeFavoriteTeam(intent.teamId)
            }

            is FavoriteIntent.SelectLeague -> changeLeagueType(intent.leagueName)
            is FavoriteIntent.ToggleNotification -> toggleNotification(intent.teamId)
            is FavoriteIntent.SelectFavoriteType -> selectedTab(intent.type)
            is FavoriteIntent.NavigateToPlayerDetail -> sendEvent(
                FavoriteEvent.NavigateToPlayerDetail(
                    intent.player.playerId.toInt()
                )
            )
        }
    }

    private fun selectedTab(tabType: FavoriteType) {
        Timber.tag("FavoriteViewModel").d("selectedTab:${tabType}")
        if (_selectedTab.value != tabType) {
            _selectedTab.value = tabType
        }
    }

    private suspend fun toggleNotification(teamId: Int) {
        Timber.tag("FavoriteViewModel").d("toggleNotification:${teamId}")
        if (_isTogglingNotification.value) return
        _isTogglingNotification.value = true
        _toggledTeamId.value = teamId
        Timber.tag("FavoriteViewModel").d("toggleNotification:${uiState.value.currentTeams}")
        val allFavoriteTeams = _favoriteTeamsByLeague.value.values.flatten()
        val currentTeam = allFavoriteTeams.find { it.team.id == teamId }
        Timber.tag("FavoriteViewModel").d("toggleNotificatioxn:${currentTeam}")
        if (currentTeam == null) {
            _isTogglingNotification.value = false
            _toggledTeamId.value = null
            sendEvent(FavoriteEvent.ShowMessage("Không tìm thấy câu lạc bộ"))
            return
        }
        val newNotificationStatus = !currentTeam.enableNotification

        val result = toggleTeamNotificationUseCase(teamId, newNotificationStatus)
        result.fold(
            onSuccess = {
                Timber.tag("FavoriteViewModel").d("toggleNotification: $it")
                val message = if (newNotificationStatus) {
                    "Đã bật thông báo cho câu lạc bộ ${currentTeam.team.name}"
                } else {
                    "Đã tắt thông báo cho câu lạc bộ ${currentTeam.team.name}"
                }
                sendEvent(FavoriteEvent.ShowMessage(message))
            },
            onFailure = { error ->
                Timber.tag("FavoriteViewModel")
                    .d("Failed to update notifications: ${error.message}")
                sendEvent(FavoriteEvent.ShowMessage("Failed to update notifications: ${error.message}"))
            }
        )
        _isTogglingNotification.value = false
        _toggledTeamId.value = null
    }

    private suspend fun removeFavoriteTeam(teamId: Int) {
        val result = removeFavoriteTeamUseCase(teamId)

        result.onSuccess {
            sendEvent(FavoriteEvent.ShowMessage("Đã xóa câu lạc bộ khỏi danh sách yêu thích"))
        }.onFailure { error ->
            sendEvent(FavoriteEvent.ShowMessage("Không thể xóa team :${error.message}"))
        }
    }

    private fun removeFavoritePlayer(playerId: Int) {
        viewModelScope.launch {
            val result = removeFavoritePlayerUseCase(playerId.toString())
            result.onSuccess {
                sendEvent(FavoriteEvent.ShowMessage("Đã xóa cầu thủ khỏi danh sách yêu thích"))
            }.onFailure { error ->
                sendEvent(FavoriteEvent.ShowMessage("Không thể xóa cầu thủ :${error.message}"))
            }
        }
    }

    private fun changeLeagueType(league: AvailableLeague) {
        val currentLeagueType = _selectedLeague.value
        if (currentLeagueType != league) {
            _selectedLeague.value = league
        }
    }
}