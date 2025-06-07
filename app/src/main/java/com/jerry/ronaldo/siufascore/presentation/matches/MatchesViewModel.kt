package com.jerry.ronaldo.siufascore.presentation.matches

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.usecase.GetLeagueInfoUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.GetMatchesByLeagueUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.GetStandingByLeagueUseCase
import com.jerry.ronaldo.siufascore.presentation.mapper.mapToTeamStandingItems
import com.jerry.ronaldo.siufascore.utils.MatchStatus
import com.jerry.ronaldo.siufascore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val getMatchesByLeagueUseCase: GetMatchesByLeagueUseCase,
    private val getStandingByLeagueUseCase: GetStandingByLeagueUseCase,
    private val getLeagueInfoUseCase: GetLeagueInfoUseCase
) : BaseViewModel<MatchesIntent, MatchesState, MatchesEffect>() {
    private val _uiState = MutableStateFlow(MatchesState())
    override val uiState: StateFlow<MatchesState>
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            intent
                .debounce(300)
                .collect { receivedIntent ->
                    processIntent(receivedIntent)
                }
        }
    }

    override suspend fun processIntent(intent: MatchesIntent) {
        when (intent) {
            is MatchesIntent.LoadMatchesByLeague -> {
                getLeagueInfo(intent.competitionId)
            }

            MatchesIntent.RefreshData -> {
                refreshData()
            }

            is MatchesIntent.SetCompetition -> {
                setCompetition(intent.competitionId)
            }

            is MatchesIntent.SetMatchday -> {
                setMatchday(intent.matchDay)
            }

            is MatchesIntent.NavigateToDetailMatch -> {
                sendEvent(MatchesEffect.NavigateToDetailMatch(intent.matchId))
            }
        }
    }

    private fun updateState(newState: (MatchesState) -> MatchesState) {
        _uiState.update(newState)
    }

    private suspend fun loadMatchesAndStandingByLeague(
        competitionId: String,
        matchDay: Int
    ) {
        updateState {
            it.copy(
                isLoading = true,
                error = null,
                competitionId = competitionId,
                currentMatchday = matchDay
            )
        }

        val matchesDeferred = viewModelScope.async {
            getMatchesByLeagueUseCase(competitionId, matchDay)
        }
        val standingDeferred = viewModelScope.async {
            getStandingByLeagueUseCase(competitionId, matchDay)
        }
        try {
            val matchesResult = matchesDeferred.await()
            val standingResult = standingDeferred.await()
            when {
                matchesResult is Resource.Success && standingResult is Resource.Success -> {
                    val standingItems = standingResult.data.mapToTeamStandingItems()
                    val allMatches = matchesResult.data
                    val liveMatches = allMatches.filter {
                        MatchStatus.from(it.status) == MatchStatus.FINISHED
                    }
                    updateState {
                        it.copy(
                            matches = allMatches,
                            liveMatches = liveMatches,
                            standingItem = standingItems,
                            availableMatchday = (1..38).toList(),
                            currentMatchday = matchesResult.data.firstOrNull()?.season?.currentMatchday,
                            isLoading = false,
                            error = null
                        )
                    }
                }

                matchesResult is Resource.Error -> {
                    updateState {
                        it.copy(
                            isLoading = false,
                            error = matchesResult.exception.message
                        )
                    }
                    sendEvent(
                        MatchesEffect.ShowError(
                            matchesResult.exception.message ?: "Lỗi khi tải trận đấu"
                        )
                    )
                }

                standingResult is Resource.Error -> {
                    if (matchesResult is Resource.Success) {
                        updateState {
                            it.copy(
                                matches = matchesResult.data,
                                isLoading = false,
                                error = null
                            )
                        }
                    } else {
                        updateState {
                            it.copy(
                                isLoading = false,
                                error = standingResult.exception.message
                            )
                        }
                        sendEvent(
                            MatchesEffect.ShowError(
                                standingResult.exception.message ?: "Lỗi khi tải bảng đấu"
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    isLoading = false,
                    error = e.message
                )
            }
            sendEvent(MatchesEffect.ShowError(e.message ?: "Lỗi khi tải dữ liệu"))
        }
    }

    private suspend fun getLeagueInfo(competitionId: String) {
        when (val leagueInfoResult = getLeagueInfoUseCase(competitionId)) {
            is Resource.Error -> {
                sendEvent(
                    MatchesEffect.ShowError(
                        leagueInfoResult.exception.message ?: "Lỗi khi tải thông tin đấu trường"
                    )
                )
            }

            Resource.Loading -> {
                updateState {
                    it.copy(
                        isLoading = true,
                        error = null
                    )
                }
            }

            is Resource.Success -> {
                val competition = leagueInfoResult.data
                updateState {
                    it.copy(
                        competitionId = competition.code,
                        currentMatchday = competition.currentSeason.currentMatchday,
                        competionInfo = competition,
                        isLoading = false,
                        error = null
                    )
                }
                loadMatchesAndStandingByLeague(
                    competitionId,
                    competition.currentSeason.currentMatchday
                )
            }
        }
    }

    private suspend fun setMatchday(matchDay: Int) {
        val currentState = _uiState.value
        currentState.competitionId?.let { competitionId ->
            loadMatchesAndStandingByLeague(competitionId, matchDay)
            updateState {
                it.copy(
                    currentMatchday = matchDay
                )
            }
        }
    }

    private suspend fun setCompetition(competitionId: String) {
        val currentMatchday = _uiState.value.currentMatchday
        currentMatchday?.let { matchday ->
            loadMatchesAndStandingByLeague(competitionId, matchday)
            updateState {
                it.copy(
                    competitionId = competitionId
                )
            }
        }
    }

    private suspend fun refreshData() {
        val currentState = _uiState.value
        if (currentState.competitionId != null && currentState.currentMatchday != null) {
            loadMatchesAndStandingByLeague(currentState.competitionId, currentState.currentMatchday)
        }
    }


}


