package com.jerry.ronaldo.siufascore.presentation.favorite

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.GetFavoriteTeamsByLeagueUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.RemoveFavoriteTeamUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.ToggleNotificationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val getFavoriteTeamsByLeagueUseCase: GetFavoriteTeamsByLeagueUseCase,
    private val toggleNotificationUseCase: ToggleNotificationUseCase,
    private val removeFavoriteTeamUseCase: RemoveFavoriteTeamUseCase
) : BaseViewModel<FavoriteIntent, FavoriteUiState, FavoriteEvent>() {
    private val _selectedLeague = MutableStateFlow(AvailableLeague.DEFAULT)
    private val _isTogglingNotification = MutableStateFlow(false)
    private val _toggledTeamId = MutableStateFlow<Int?>(null)

    private val _favoriteTeamsByLeague: StateFlow<Map<String, List<FavoriteTeam>>> =
        getFavoriteTeamsByLeagueUseCase().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyMap()
        )
    private val basicState = combine(
        _selectedLeague,
        _isTogglingNotification,
        _toggledTeamId
    ) { selectedLeague, isTogglingNotification, toggledTeamId ->
        Triple(selectedLeague, isTogglingNotification, toggledTeamId)
    }
    override val uiState: StateFlow<FavoriteUiState>
        get() = combine(
            basicState,
            _favoriteTeamsByLeague
        ) { basic, favoriteTeams ->
            val (selectedLeague, isTogglingNotification, toggledTeamId) = basic
            val isLoading = favoriteTeams.isEmpty()
            FavoriteUiState(
                favoriteTeamsByLeague = favoriteTeams,
                selectedLeagueType = selectedLeague,
                isLoading = isLoading,
                isTogglingNotification = isTogglingNotification,
                toggledTeamId = toggledTeamId
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FavoriteUiState(isLoading = true)
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

        val result = toggleNotificationUseCase(teamId, newNotificationStatus)
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
                Timber.tag("FavoriteViewModel").d("Failed to update notifications: ${error.message}")
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

    private fun changeLeagueType(league: AvailableLeague) {
        val currentLeagueType = _selectedLeague.value
        if (currentLeagueType != league) {
            _selectedLeague.value = league
        }
    }
}