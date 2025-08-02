package com.jerry.ronaldo.siufascore.presentation.matches

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetLeagueInfoUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetMatchesByLeagueUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetStandingByLeagueUseCase
import com.jerry.ronaldo.siufascore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class MatchesViewModel @Inject constructor(
    private val getMatchesByLeagueUseCase: GetMatchesByLeagueUseCase,
    private val getStandingByLeagueUseCase: GetStandingByLeagueUseCase,
    private val getLeagueInfoUseCase: GetLeagueInfoUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<MatchesIntent, MatchesState, MatchesEffect>() {

    // Lấy arguments từ navigation (nếu có)
    private val _competitionId = MutableStateFlow(
        savedStateHandle.get<Int>("competitionId") ?: 39
    )
    private val _matchday = MutableStateFlow<String?>(null)

    // Trigger để control data loading
    private val _refreshTrigger = MutableStateFlow(Unit)

    // League info flow - lazy loading khi có subscriber
    @OptIn(ExperimentalCoroutinesApi::class)
    private val leagueInfoFlow = combine(
        _competitionId,
        _refreshTrigger
    ) { competitionId, _ ->
        competitionId
    }.flatMapLatest { competitionId ->
        getLeagueInfoUseCase(competitionId)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.Loading
    )
    private val currentMatchdayFlow = combine(
        _matchday,
        leagueInfoFlow
    ) { userSelectedMatchday, leagueResource ->
        when {
            userSelectedMatchday != null -> userSelectedMatchday
            leagueResource is Resource.Success -> leagueResource.data.currentMatchday.takeIf {
                it.isNotEmpty()
            }?.get(0)

            else -> null
        }
    }.filterNotNull()

    // Matches flow - depends on league info
    @OptIn(ExperimentalCoroutinesApi::class)
    private val matchesFlow = combine(
        _competitionId,
        currentMatchdayFlow,
        _refreshTrigger
    ) { competitionId, matchday, _ ->
        Pair(competitionId, matchday)
    }.flatMapLatest { (competitionId, matchday) ->
        getMatchesByLeagueUseCase(
            competitionId = competitionId,
            matchDay = matchday
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = Resource.Loading
    )

      // Standings flow - depends on league info
      @OptIn(ExperimentalCoroutinesApi::class)
      private val standingsFlow = combine(
          _competitionId,
          _refreshTrigger
      ) { competitionId, _ ->
            competitionId
      }.flatMapLatest {competitionId ->
          getStandingByLeagueUseCase(competitionId = competitionId)
      }.stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5_000),
          initialValue = Resource.Loading
      )

    // Combined UI state
    override val uiState: StateFlow<MatchesState> = combine(
        _competitionId,
        currentMatchdayFlow,
        leagueInfoFlow,
         matchesFlow,
        standingsFlow
    ) { competitionId,currentMatchday, leagueInfo,matches,standings ->
        MatchesState(
            competitionId = competitionId,
            competionInfo = (leagueInfo as? Resource.Success)?.data,
            currentMatchday = currentMatchday,
            isLoading = leagueInfo is Resource.Loading,
            error = (leagueInfo as? Resource.Error)?.exception?.message,
            matches = (matches as? Resource.Success)?.data ?: emptyList(),
            isMatchesLoading = matches is Resource.Loading,
            matchesError = (matches as? Resource.Error)?.exception?.message,
            standings = (standings as? Resource.Success)?.data ?: emptyList(),
            isStandingsLoading = standings is Resource.Loading,
            standingError = (standings as? Resource.Error)?.exception?.message,
            availableMatchday = (1..38).toList()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MatchesState()
    )

    override suspend fun processIntent(intent: MatchesIntent) {
        when (intent) {
            is MatchesIntent.LoadMatchesByLeague -> {
                // Trigger data loading bằng cách emit trigger
                setCompetition(intent.competitionId)
            }

            MatchesIntent.RefreshData -> {
                // Refresh bằng cách emit trigger mới
                refreshData()
            }

            is MatchesIntent.SetMatchday -> {
                // Implement logic để change matchday
                setMatchday(intent.matchDay)
            }

            is MatchesIntent.SetCompetition -> {
                // Implement logic để change competition
                setCompetition(intent.competitionId)
            }

            is MatchesIntent.NavigateToDetailMatch -> {
                sendEvent(MatchesEffect.NavigateToDetailMatch(intent.matchId))
            }
        }
    }

    /*private fun updateState(newState: (MatchesState) -> MatchesState) {
        _uiState.update(newState)
    }
*/

    private fun setMatchday(matchday: String) {
        _matchday.value = "Regular Season - $matchday"
        // Flow sẽ tự động trigger lại matches và standings với matchday mới
    }

    private fun setCompetition(competitionId: Int) {
        _competitionId.value = competitionId
        _matchday.value = null // Reset matchday để dùng default từ league
    }

    private suspend fun refreshData() {
        _refreshTrigger.emit(Unit)
    }

}



