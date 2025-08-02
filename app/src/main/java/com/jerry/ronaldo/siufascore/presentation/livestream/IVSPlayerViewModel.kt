package com.jerry.ronaldo.siufascore.presentation.livestream

import androidx.lifecycle.viewModelScope
import com.amazonaws.ivs.player.Player
import com.jerry.ronaldo.siufascore.base.BaseViewModel
import com.jerry.ronaldo.siufascore.data.model.LiveComment
import com.jerry.ronaldo.siufascore.domain.repository.LiveStreamRepository
import com.jerry.ronaldo.siufascore.domain.usecase.auth.GetCurrentUserUseCase
import com.jerry.ronaldo.siufascore.utils.IvsPlayerManager
import com.jerry.ronaldo.siufascore.utils.PlayerState
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.StreamQuality
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val HLS =
    "https://fcc3ddae59ed.us-west-2.playback.live-video.net/api/video/v1/us-west-2.893648527354.channel.DmumNckWFTqz.m3u8"

@HiltViewModel
class IVSPlayerViewModel @Inject constructor(
    private val liveStreamRepository: LiveStreamRepository,
    private val ivsPlayer: IvsPlayerManager,
    private val getCurrentUser: GetCurrentUserUseCase
) : BaseViewModel<IVSPlayerIntent, IVSPlayerViewUiState, IVSPlayerEvent>() {
    private val _uiState = MutableStateFlow(IVSPlayerViewUiState())
    override val uiState: StateFlow<IVSPlayerViewUiState>
        get() = _uiState.asStateFlow()
    private var commentsJob: Job? = null
    private fun updateState(newState: (IVSPlayerViewUiState) -> IVSPlayerViewUiState) {
        _uiState.update { state ->
            newState(state)
        }
    }

    init {
        setupQualityCallbacks()
    }

    private fun setupQualityCallbacks() {
        ivsPlayer.setQualityCallbacks(
            onQualitiesChanged = { qualities ->
                Timber.d("Qualities updated: ${qualities.size} options")
                _uiState.value = _uiState.value.copy(availableQualities = qualities)
            },
            onQualityChanged = { quality ->
                Timber.d("Current quality changed: ${quality.displayName}")
                _uiState.value = _uiState.value.copy(currentQuality = quality)
            }
        )
    }

    override suspend fun processIntent(intent: IVSPlayerIntent) {
        when (intent) {
            is IVSPlayerIntent.HideControls -> handleHideControls()
            is IVSPlayerIntent.LoadStream -> {
                handleLoadStream()
                //ivsPlayer.loadAndPlay(HLS)
            }

            is IVSPlayerIntent.PlayPause -> handlePlayPause()
            is IVSPlayerIntent.PlayerReady -> handlePlayerReady(intent.player)
            is IVSPlayerIntent.Retry -> handleRetry()
            is IVSPlayerIntent.SetMatchId -> setMatchId(intent.matchId)
            is IVSPlayerIntent.ShowControls -> handleShowControls()
            is IVSPlayerIntent.ToggleControlsVisibility -> handleToggleControlsVisibility()
            is IVSPlayerIntent.ToggleFullscreen -> handleToggleFullScreen()
            is IVSPlayerIntent.LoadComments -> {
                getCurrentUser()
                handleLoadComment()
            }

            is IVSPlayerIntent.LoadMoreComments -> handleLoadMoreComment()
            is IVSPlayerIntent.PostComment -> handlePostComment(intent.commentText)
            IVSPlayerIntent.RefreshComments -> handleRefreshComment()
            is IVSPlayerIntent.SelectQuality -> handleSelectQuality(intent.qualityOption)

        }
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            val currentUser = getCurrentUser.invoke()
            Timber.d("Current user: $currentUser")
            updateState {
                it.copy(
                    currentUserImg = currentUser?.profilePictureUrl
                )
            }
        }
    }

    private fun handleLoadComment() {
        val matchId = _uiState.value.matchId ?: return
        Timber.d("Đang tải bình luận cho trận đấu ID: $matchId")

        commentsJob?.cancel()
        _uiState.value = _uiState.value.copy(
            isLoadingCmt = true,
            errorMessage = null,
            comments = emptyList()
        )
        commentsJob = viewModelScope.launch {
            liveStreamRepository.getComments(matchId = matchId.toString(), limit = 50)
                .collect { result ->
                    when (result) {
                        is Resource.Error -> {
                            updateState {
                                it.copy(
                                    isLoadingCmt = false,
                                    errorMessage = result.exception.message
                                        ?: "Lỗi không rõ khi tải comments"
                                )
                            }
                            sendEvent(
                                IVSPlayerEvent.ShowError(
                                    result.exception.message ?: "Lỗi không rõ khi tải comments"
                                )
                            )
                        }

                        Resource.Loading -> updateState { it.copy(isLoadingCmt = true) }
                        is Resource.Success -> {
                            updateState {
                                it.copy(
                                    isLoadingCmt = false,
                                    comments = result.data,
                                    errorMessage = null
                                )
                            }
                        }
                    }
                }
        }
    }



    private fun handleSelectQuality(qualityOption: StreamQuality) {
        ivsPlayer.setQuality(qualityOption)
        val updatedQualities = _uiState.value.availableQualities.map { option ->
            option.copy(
                isSelected = when {
                    qualityOption.isAutoQuality && option.isAutoQuality -> true
                    !qualityOption.isAutoQuality && !option.isAutoQuality &&
                            option.quality?.name == qualityOption.quality?.name -> true

                    else -> false
                }
            )
        }
        updateState {
            it.copy(
                currentQuality = qualityOption,
                availableQualities = updatedQualities
            )
        }
    }

    private suspend fun handleLoadMoreComment() {
        val matchId = _uiState.value.matchId ?: return
        if (_uiState.value.isLoadingMore || !_uiState.value.hasMoreComments) {
            Timber.d("Đang tải thêm bình luận hoặc không còn bình luận nào để tải")
            return
        }
        updateState { it.copy(isLoadingCmt = true) }
        try {
            liveStreamRepository.getCommentsWithPagination(
                matchId = matchId.toString(),
                limit = 20,
                startAfterKey = _uiState.value.lastCommentKey
            ).collect { result ->
                when (result) {
                    is Resource.Error -> updateState {
                        it.copy(
                            isLoadingMore = false,
                            errorMessage = result.exception.message
                                ?: "Lỗi không rõ khi tải thêm bình luận"
                        )
                    }.also {
                        sendEvent(
                            IVSPlayerEvent.ShowError(
                                result.exception.message ?: "Lỗi không rõ khi tải thêm bình luận"
                            )
                        )
                    }

                    Resource.Loading -> {

                    }

                    is Resource.Success -> {
                        val cmtsResult = result.data
                        val currentCmt = _uiState.value.comments.toMutableList()
                        currentCmt.addAll(0, cmtsResult.comments)
                        updateState {
                            it.copy(
                                isLoadingMore = false,
                                comments = currentCmt,
                                hasMoreComments = cmtsResult.hasMore,
                                lastCommentKey = cmtsResult.lastKey
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            updateState {
                it.copy(
                    isLoadingMore = false,
                    errorMessage = e.message ?: "Lỗi không rõ khi tải thêm bình luận"
                )
            }.also {
                sendEvent(IVSPlayerEvent.ShowError(e.message ?: "Tải bình luận không thành công"))
            }
        }
    }

    private suspend fun handlePostComment(commentText: String) {
        val matchId = _uiState.value.matchId ?: return
        if (commentText.isBlank()) {
            Timber.d("Bình luận rỗng, không thể đăng")
            sendEvent(IVSPlayerEvent.ShowError("Bình luận không được để trống"))
            return
        }
        updateState {
            it.copy(isPosting = true)
        }
        val comment = LiveComment(
            comment = commentText.trim(),
            timestamp = System.currentTimeMillis()
        )
        Timber.d("Đang đăng bình luận: ${comment.comment} cho trận đấu ID: $matchId")
        when (val result = liveStreamRepository.postComment(matchId.toString(), comment)) {
            is Resource.Success -> {
                Timber.d("Bình luận đã được đăng thành công")
                updateState {
                    it.copy(
                        isPosting = false,
                    )
                }
                sendEvent(IVSPlayerEvent.CommentPostedSuccessfully)
                sendEvent(IVSPlayerEvent.ScrollToBottom)
            }

            is Resource.Error -> {
                Timber.e(result.exception, "Lỗi khi đăng bình luận")
                updateState {
                    it.copy(
                        isPosting = false,
                        errorMessage = result.exception.message ?: "Lỗi không rõ khi đăng bình luận"
                    )
                }
                sendEvent(
                    IVSPlayerEvent.ShowError(
                        result.exception.message ?: "Lỗi không rõ khi đăng bình luận"
                    )
                )
            }

            Resource.Loading -> {

            }
        }
    }

    private fun handleRefreshComment() {
        Timber.d("Đang làm mới bình luận")
        updateState {
            it.copy(
                isLoadingCmt = true,
                errorMessage = null,
                comments = emptyList()
            )
        }
        handleLoadComment()
    }

    private fun handleClearError() {
        Timber.d("Đang xóa thông báo lỗi")
        updateState {
            it.copy(
                errorMessage = null
            )
        }
    }

    private fun handlePlayerReady(player: Player) {
        Timber.d("Đang setup")
        ivsPlayer.setPlayer(player)
        ivsPlayer.initializePlayer(
            onStateChanged = { state ->
                Timber.d("Player state changed: $state")
                handlePlayerStateChange(state)
                if (state == Player.State.READY) {
                    ivsPlayer.getQualityOptions()
                }
            },
            onVideoSizeChanged = { videoSize ->
                //handleVideoSizeChanged(size)
            },
            onError = { error ->
                handleError(error)
            }
        )
    }

    private fun setMatchId(matchId: Int) {
        Timber.d("Setting match ID: $matchId")
        updateState {
            it.copy(
                matchId = matchId
            )
        }
    }

    private suspend fun handleLoadStream() {
        val matchId = _uiState.value.matchId ?: return
        updateState {
            it.copy(
                isLoading = true,
                errorMessage = null,
                playerState = PlayerState.Loading
            )
        }
        try {
            Timber.d("Đang tải luồng trực tiếp cho ID trận đấu: $matchId")
            when (val result = liveStreamRepository.getLiveStreamById(matchId.toLong())) {
                is Resource.Success -> {
                    val playbackUrl = result.data.hlsUrl
                    _uiState.update {
                        it.copy(
                            playbackUrl = playbackUrl,
                            matchInfo = result.data.matchInfo,
                            isLoading = false
                        )
                    }
                    Timber.d("Playback URL loaded: $playbackUrl")
                    ivsPlayer.loadAndPlay(playbackUrl)
                }

                is Resource.Error -> {
                    val errorMessage = result.exception.message ?: "Unknown error occurred"
                    Timber.e("Error loading playback URL: $errorMessage")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = errorMessage,
                        playerState = PlayerState.Error
                    )
                    sendEvent(IVSPlayerEvent.ShowError(errorMessage))
                }

                is Resource.Loading -> {
                    updateState {
                        it.copy(isLoading = true)
                    }
                }
            }
        } catch (e: Exception) {
            val errorMessage = e.message ?: "Unknown error occurred"
            Timber.e(e, "Error loading playback URL")
            updateState {
                it.copy(
                    isLoading = false,
                    errorMessage = errorMessage,
                    playerState = PlayerState.Error
                )
            }
            sendEvent(IVSPlayerEvent.ShowError(errorMessage))
        }
    }

    private fun handlePlayPause() {
        when (_uiState.value.playerState) {
            PlayerState.Playing -> {
                ivsPlayer.pause()

            }

            PlayerState.Paused, PlayerState.Ready -> {
                ivsPlayer.play()

            }

            PlayerState.Ended -> {
                ivsPlayer.replay()

            }

            PlayerState.Error -> {
                viewModelScope.launch {
                    handleRetry()
                }
            }

            else -> {
                //ignore
            }
        }
    }

    private suspend fun handleRetry() {
        updateState { it.copy(errorMessage = null) }
        handleLoadStream()
    }

    private suspend fun handleToggleFullScreen() {
        val isFullScreen = _uiState.value.isFullscreen
        updateState {
            it.copy(
                isFullscreen = !isFullScreen
            )
        }
        if (isFullScreen) {
            sendEvent(IVSPlayerEvent.ExitFullscreen)
        } else {
            sendEvent(IVSPlayerEvent.EnterFullscreen)
        }
    }

    private suspend fun handleShowControls() {
        updateState { it.copy(isControlsVisible = true) }
        if (shouldAutoHideControls()) {
            sendEvent(IVSPlayerEvent.StartControlsTimer)
        }
    }

    private fun handleHideControls() {
        if (shouldAutoHideControls()) {
            updateState { it.copy(isControlsVisible = false) }
        }
    }

    private suspend fun handleToggleControlsVisibility() {
        if (_uiState.value.isControlsVisible) {
            handleHideControls()
        } else {
            handleShowControls()
        }
    }

    private fun handlePlayerStateChange(state: Player.State) {
        viewModelScope.launch {
            val playerState = when (state) {
                Player.State.BUFFERING -> PlayerState.Buffering
                Player.State.READY -> {
                    ivsPlayer.setAutoMaxQuality()
                    PlayerState.Ready
                }

                Player.State.PLAYING -> {
                    if (_uiState.value.isControlsVisible && shouldAutoHideControls()) {
                        sendEvent(IVSPlayerEvent.StartControlsTimer)
                    }
                    PlayerState.Playing
                }

                Player.State.ENDED -> {
                    sendEvent(IVSPlayerEvent.StopControlsTimer)
                    handleShowControls()
                    PlayerState.Ended
                }

                Player.State.IDLE -> {
                    sendEvent(IVSPlayerEvent.StopControlsTimer)
                    handleShowControls()
                    PlayerState.Paused
                }
            }
            updateState {
                it.copy(
                    playerState = playerState,
                    isLoading = false,
                    shouldAutoHideControls = shouldAutoHideControls()
                )
            }
        }
    }

    private fun shouldAutoHideControls(): Boolean {
        return _uiState.value.playerState == PlayerState.Playing &&
                _uiState.value.errorMessage == null
    }

    private fun handleError(error: Throwable) {
        viewModelScope.launch {
            val errorMessage = error.message ?: "Đã có lỗi xảy ra"
            Timber.e(error, "Đã có lỗi xảy ra")

            updateState {
                it.copy(
                    isLoading = false,
                    playerState = PlayerState.Error,
                    errorMessage = errorMessage
                )
            }

            sendEvent(IVSPlayerEvent.StopControlsTimer)
            sendEvent(IVSPlayerEvent.ShowError(errorMessage))
            handleShowControls()
        }
    }

    fun release() {
        Timber.d("Giải phóng tài nguyên")
        ivsPlayer.release()
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("IVSPlayerViewModel onCleared")
        commentsJob?.cancel()
    }

}