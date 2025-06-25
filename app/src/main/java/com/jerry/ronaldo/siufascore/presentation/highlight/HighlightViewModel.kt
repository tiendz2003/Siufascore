package com.jerry.ronaldo.siufascore.presentation.highlight

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.domain.model.VideoItem
import com.jerry.ronaldo.siufascore.domain.usecase.highlight.GetListHighLightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HighlightViewModel @Inject constructor(
    private val getHighlightUseCase: GetListHighLightUseCase,
) : BaseViewModel<HighLightIntent, HighLightUiState, HighLightEffect>() {
    private val _playlistId = MutableStateFlow("premier_league")
    private val _leagueId = MutableStateFlow("PL")
    private val _refreshTrigger = MutableStateFlow(Unit)
    private val _videoId = MutableStateFlow<String?>(null)
    @OptIn(ExperimentalCoroutinesApi::class)
    val playlistVideo: Flow<PagingData<VideoItem>> = combine(
        _playlistId,
        _refreshTrigger
    ) { playlistId, _ ->
        playlistId
    }.flatMapLatest { playlistId ->
        getHighlightUseCase(playlistId)
    }.cachedIn(
        scope = viewModelScope
    )

    override val uiState: StateFlow<HighLightUiState> = combine(
        _videoId,
        _playlistId,
        _leagueId,
    ) { videoId, playlistId, leagueId ->

        HighLightUiState(
            playlistId = playlistId,
            selectedLeague = leagueId,
            videoId = videoId,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HighLightUiState()
    )

    override suspend fun processIntent(intent: HighLightIntent) {
        when (intent) {
            is HighLightIntent.LoadHighlightByLeague -> loadHighlightsByLeague(intent.playlistId)
            is HighLightIntent.RefreshData -> refreshHighlight()
            is HighLightIntent.SetPlaylistId -> {
                setPlaylistId(intent.playlistId)
            }

            is HighLightIntent.SetVideoId -> {
                setVideoId(intent.videoId)
            }


        }
    }

    private fun refreshHighlight() {
        viewModelScope.launch {
            _refreshTrigger.emit(Unit)
        }
    }

    private fun loadHighlightsByLeague(playlistId: String) {
        viewModelScope.launch {
            try {
                _playlistId.emit(playlistId)
            } catch (e: Exception) {
                sendEvent(HighLightEffect.ShowError("Lỗi tải highlight:${e.message}"))
            }
        }
    }

    private fun setPlaylistId(leagueId: String) {
        viewModelScope.launch {
            _playlistId.emit(leagueId)
            _leagueId.emit(leagueId)
        }
    }

    private fun setVideoId(videoId: String) {
        viewModelScope.launch {
            _videoId.emit(videoId)
        }
    }

}