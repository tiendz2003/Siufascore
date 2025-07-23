package com.jerry.ronaldo.siufascore.presentation.team

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.data.model.ResponseTeamStatistics
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.AddFavoriteTeamUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.ObserveFavoriteTeamsUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.favortite.RemoveFavoriteTeamUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetDetailTeamUseCase
import com.jerry.ronaldo.siufascore.presentation.player.PlayerDetailTab
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.getCurrentSeason
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = DetailTeamViewModel.Factory::class)
class DetailTeamViewModel @AssistedInject constructor(
    private val getDetailTeamUseCase: GetDetailTeamUseCase,
    private val addFavoriteTeamUseCase: AddFavoriteTeamUseCase,
    private val observeFavoriteTeamUseCase: ObserveFavoriteTeamsUseCase,
    private val removeFavoriteTeamUseCase: RemoveFavoriteTeamUseCase,
    @Assisted("teamId") private val teamId: Int,
    @Assisted("leagueId") private val leagueId: Int,
) : BaseViewModel<DetailTeamIntent, DetailTeamUiState, DetailTeamEvent>() {

    private val _season = MutableStateFlow(getCurrentSeason())
    private val _selectedTab = MutableStateFlow(PlayerDetailTab.OVERVIEW)
    private val _isTogglingFavorite = MutableStateFlow(false) // Đổi tên cho rõ nghĩa

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _teamStatisticResource: StateFlow<Resource<ResponseTeamStatistics>> =
        _season.flatMapLatest { season ->
            getDetailTeamUseCase(teamId, leagueId, season)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Resource.Loading // Giá trị khởi tạo là Loading
        )

    private val _isFavoriteTeam: StateFlow<Boolean> = observeFavoriteTeamUseCase()
        .map { favoriteTeams -> favoriteTeams.any { it.team.id == teamId } }
        .catch { emit(false) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    override val uiState: StateFlow<DetailTeamUiState> = combine(
        _teamStatisticResource,
        _selectedTab,
        _isFavoriteTeam,
        _isTogglingFavorite
    ) { resource, selectedTab, isFavorite, isToggling ->
        DetailTeamUiState(
            // Lấy các giá trị trực tiếp từ resource trong này
            isLoading = resource is Resource.Loading,
            error = if (resource is Resource.Error) resource.exception.message else null,
            teamStatistic = if (resource is Resource.Success) resource.data else null,

            selectedTab = selectedTab,
            isFavoriteTeam = isFavorite,
            isAddingFavorite = isToggling // Cập nhật trạng thái
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailTeamUiState(isLoading = true)
    )

    // Dùng một hàm handleIntent không phải suspend để xử lý action từ UI
    override suspend fun processIntent(intent: DetailTeamIntent) {
        when (intent) {
            is DetailTeamIntent.OnToggleFavoriteTeam -> toggleFavoriteTeam()
            is DetailTeamIntent.SelectTab -> _selectedTab.value = intent.tab
        }
    }

    private suspend fun toggleFavoriteTeam() {
        val teamData = uiState.value.teamStatistic ?: return
        if (uiState.value.isAddingFavorite) return

        _isTogglingFavorite.value = true

        val result = if (uiState.value.isFavoriteTeam) {
            removeFavoriteTeamUseCase(teamId)
        } else {
            // Tái sử dụng object có sẵn, clean hơn
            val teamInfo = teamData.team.let { TeamInfo(it.id, it.name, it.logo) }
            val leagueInfo = teamData.league.let {
                LeagueInfo(
                    it.id,
                    it.name,
                    it.country,
                    it.logo,
                    it.flag,
                    it.season
                )
            }
            addFavoriteTeamUseCase(teamInfo, leagueInfo)
        }

        result.onFailure {
            // TODO: Gửi event báo lỗi ra UI
        }

        _isTogglingFavorite.value = false

    }


    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("teamId") teamId: Int,
            @Assisted("leagueId") leagueId: Int
        ): DetailTeamViewModel
    }
}