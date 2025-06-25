package com.jerry.ronaldo.siufascore.presentation.matches

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetDetailMatchUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetH2HInfoUseCase
import com.jerry.ronaldo.siufascore.presentation.matches.screen.detail.DetailMatchEvent
import com.jerry.ronaldo.siufascore.presentation.matches.screen.detail.DetailMatchIntent
import com.jerry.ronaldo.siufascore.presentation.matches.screen.detail.DetailMatchState
import com.jerry.ronaldo.siufascore.presentation.matches.screen.detail.MatchDetailTab
import com.jerry.ronaldo.siufascore.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@HiltViewModel(assistedFactory = DetailMatchViewModel.Factory::class)
class DetailMatchViewModel @AssistedInject constructor(
    val getMatchDetailUseCase: GetDetailMatchUseCase,
    val getH2HInfoUseCase: GetH2HInfoUseCase,
    @Assisted val matchId: Int
) : BaseViewModel<DetailMatchIntent, DetailMatchState, DetailMatchEvent>() {
    private val _selectedTab = MutableStateFlow(MatchDetailTab.OVERVIEW)
    private val _refreshTrigger = MutableStateFlow(Unit)

    @OptIn(ExperimentalCoroutinesApi::class)
    val matchDetail = _refreshTrigger.flatMapLatest {
        getMatchDetailUseCase(matchId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.Loading
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val h2hInfo = combine(
        matchDetail, _selectedTab
    ) { detail, tab ->
        if (tab == MatchDetailTab.H2H && detail is Resource.Success) {
            val info = detail.data.match
            Pair(info.homeTeam.id, info.awayTeam.id)
        } else {
            null
        }
    }.distinctUntilChanged().flatMapLatest { ids ->
        if (ids != null) {
            getH2HInfoUseCase(ids.first, ids.second)
        } else {
            flowOf(null)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = null
    )
    override val uiState: StateFlow<DetailMatchState>
        get() = combine(
            _selectedTab,
            matchDetail,
            h2hInfo
        ) { selectedTab, matchDetail, h2h ->
            val fixtureDetail = if (matchDetail is Resource.Success) {
                matchDetail.data
            } else {
                null
            }
            DetailMatchState(
                isLoading = matchDetail is Resource.Loading,
                fixtureDetail = fixtureDetail,
                error = if (matchDetail is Resource.Error) {
                    matchDetail.exception.message ?: "Đã xảy ra lỗi không xác định"
                } else {
                    null
                },
                selectedTab = selectedTab,
                homeTeamId = fixtureDetail?.match?.homeTeam?.id ?: 0,
                awayTeamId = fixtureDetail?.match?.awayTeam?.id ?: 0,
                h2hInfo = if (h2h is Resource.Success) {
                    h2h.data
                } else {
                    null
                },
                h2hLoading = h2h is Resource.Loading,
                h2hError = if (h2h is Resource.Error) {
                    h2h.exception.message ?: "Đã xảy ra lỗi không xác định"
                } else {
                    null
                }
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DetailMatchState(isLoading = true)
        )

    override suspend fun processIntent(intent: DetailMatchIntent) {
        when (intent) {
            DetailMatchIntent.LoadH2H -> {
                refreshData()
            }

            DetailMatchIntent.LoadStatistic -> {
                loadFixtureDetail()
            }

            is DetailMatchIntent.SelectTab -> {
                selectTab(intent.tab)
            }
        }
    }

    private fun loadFixtureDetail() {
        _refreshTrigger.value = Unit
    }

    private fun selectTab(tab: MatchDetailTab) {
        _selectedTab.value = tab
    }

    fun refreshData() {
        _refreshTrigger.value = Unit
    }


    @AssistedFactory
    interface Factory {
        fun create(matchId: Int): DetailMatchViewModel
    }
}