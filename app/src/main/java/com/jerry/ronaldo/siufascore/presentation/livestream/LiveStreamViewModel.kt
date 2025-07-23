package com.jerry.ronaldo.siufascore.presentation.livestream

import androidx.lifecycle.viewModelScope
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.usecase.football.GetMatchesByLeagueUseCase
import com.jerry.ronaldo.siufascore.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveStreamViewModel @Inject constructor(
    private val getMatchesByLeagueUseCase: GetMatchesByLeagueUseCase,
) : BaseViewModel<LiveStreamIntent, LiveStreamUiState, LiveStreamEffect>() {
    private val _uiState = MutableStateFlow(LiveStreamUiState())
    override val uiState: StateFlow<LiveStreamUiState>
        get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            loadTodayMatches()
        }
    }

    private fun updateState(update: (LiveStreamUiState) -> LiveStreamUiState) {
        _uiState.update { state ->
            update(state)
        }
    }

    override suspend fun processIntent(intent: LiveStreamIntent) {
        when (intent) {
            is LiveStreamIntent.SetSelectedLeague -> {
                // Handle setting selected league
            }

            is LiveStreamIntent.SetSelectedMatch -> {
                // Handle setting selected match
            }

            is LiveStreamIntent.RefreshData -> {
                // Handle refreshing data
            }
        }
    }

    private suspend fun loadTodayMatches() {
        if (_uiState.value.isLoading) return

        getMatchesByLeagueUseCase(
            competitionId = 39,
            matchDay = "Regular Season - 38"
        ).onStart {
            updateState { state ->
                state.copy(isLoading = true, errorMessage = null)
            }
        }.catch { exception ->
            updateState { state ->
                state.copy(
                    isLoading = false,
                    errorMessage = exception.message
                )
            }
        }
            .collect { result ->
                when (result) {
                    is Resource.Success -> {
                        updateState { state ->
                            state.copy(
                                isLoading = false,
                                matches = result.data
                            )
                        }
                    }

                    is Resource.Error -> {
                        updateState { state ->
                            state.copy(
                                isLoading = false,
                                errorMessage = result.exception.message
                            )
                        }
                    }

                    is Resource.Loading -> {
                        updateState { state ->
                            state.copy(isLoading = true)
                        }
                    }
                }
            }


    }
}
