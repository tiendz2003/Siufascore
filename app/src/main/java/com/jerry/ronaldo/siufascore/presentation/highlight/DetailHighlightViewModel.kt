package com.jerry.ronaldo.siufascore.presentation.highlight

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.data.model.CommentThread
import com.jerry.ronaldo.siufascore.domain.usecase.highlight.GetVideoDetailsInfoUseCase
import com.jerry.ronaldo.siufascore.domain.usecase.highlight.GetYoutubeCmtsUseCase
import com.jerry.ronaldo.siufascore.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber


@HiltViewModel(assistedFactory = DetailHighlightViewModel.Factory::class)
class DetailHighlightViewModel @AssistedInject constructor(
    private val getVideoDetailsInfoUseCase: GetVideoDetailsInfoUseCase,
    private val getVideoCmtsUseCase: GetYoutubeCmtsUseCase,
    @Assisted val videoId:String
) : BaseViewModel<DetailHighLightIntent, DetailHighLightUiState, HighLightEffect>() {
    private val _playlistId = MutableStateFlow("premier_league")
    private val _leagueId = MutableStateFlow("PL")
    private val _refreshTrigger = MutableStateFlow(Unit)
    private val _videoId = MutableStateFlow<String>(videoId)
    private val _commentsExpanded = MutableStateFlow(false)
    val isCommentsExpanded = _commentsExpanded.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    // Comments chỉ load khi expanded = true
    val videoComments: Flow<PagingData<CommentThread>> = combine(
        _videoId,
        _commentsExpanded.filter { it } // Chỉ khi expanded
    ) { videoId,_ ->
        videoId
    }.flatMapLatest { videoId ->
        getVideoCmtsUseCase(videoId)
    }.cachedIn(viewModelScope)


    @OptIn(ExperimentalCoroutinesApi::class)
    val _infoDetailVideo = _videoId.filterNotNull().distinctUntilChanged()
        .flatMapLatest { videoId ->
            getVideoDetailsInfoUseCase(videoId)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = Resource.Loading
        )
    override val uiState: StateFlow<DetailHighLightUiState> = combine(
        _videoId,
        _playlistId,
        _leagueId,
        _infoDetailVideo,
    ) { videoId, playlistId, leagueId, infoVideo ->

        DetailHighLightUiState(
            playlistId = playlistId,
            selectedLeague = leagueId,
            videoId = videoId,
            infoVideo = if (infoVideo is Resource.Success) infoVideo.data else null
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailHighLightUiState()
    )

    override suspend fun processIntent(intent: DetailHighLightIntent) {
        when (intent) {
            is DetailHighLightIntent.LoadHighlightByLeague -> loadHighlightsByLeague(intent.playlistId)
            is DetailHighLightIntent.RefreshData -> refreshHighlight()
            is DetailHighLightIntent.SetPlaylistId -> {
                setPlaylistId(intent.playlistId)
            }

            is DetailHighLightIntent.SetVideoId -> {
                setVideoId(intent.videoId)
            }

            DetailHighLightIntent.ClearVideoId -> {
                clearVideoId()
            }

            DetailHighLightIntent.ToggleComments -> {
                toggleComments()
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

    private fun clearVideoId() {
        viewModelScope.launch {
            //_videoId.emit(null)
        }
    }

    private fun toggleComments() {
        viewModelScope.launch {
            _commentsExpanded.emit(!_commentsExpanded.value)
            Timber.d("Comments expanded: ${_commentsExpanded.value}")
        }
    }
    @AssistedFactory
    interface Factory{
        fun create(videoId:String):DetailHighlightViewModel
    }
}