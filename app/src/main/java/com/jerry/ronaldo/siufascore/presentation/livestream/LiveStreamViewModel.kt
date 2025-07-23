package com.jerry.ronaldo.siufascore.presentation.livestream

import android.content.Context
import android.util.Size
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amazonaws.ivs.player.Player
import com.jerry.ronaldo.siufascore.domain.repository.LiveStreamRepository
import com.jerry.ronaldo.siufascore.utils.PlayerState
import com.jerry.ronaldo.siufascore.utils.init
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

const val HLS = "https://fcc3ddae59ed.us-west-2.playback.live-video.net/api/video/v1/us-west-2.893648527354.channel.DmumNckWFTqz.m3u8"
@HiltViewModel
class IVSPlayerViewModel @Inject constructor(
    @ApplicationContext val context:Context,
    private val liveStreamRepository: LiveStreamRepository,
) : ViewModel() {
    private lateinit var playerListener: Player.Listener
    private var player: Player? = null
    private val _onBuffering = Channel<Boolean>()
    private val _onSizeChanged = MutableSharedFlow<Size>(replay = 1)
    private val _onError = Channel<Throwable>()

    private val _playerState = MutableStateFlow<PlayerState>(PlayerState.Loading)
    val playerState = _playerState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _errorState = MutableStateFlow<String?>(null)
    val errorState = _errorState.asStateFlow()

    fun setPlayer(player:Player){
        Timber.d("Setting player")
        this.player = player
        initPlayer()
    }
    private fun initPlayer(){
        _isLoading.value = true
        _playerState.value = PlayerState.Loading
        _errorState.value = null

        playerListener = player!!.init(
            onVideoSizeChanged = {videoSizeState->
                _onSizeChanged.tryEmit(videoSizeState)
            },
            onStateChanged = {state->
                handlePlayerStateChange(state)
            },
            onError = {
                _onError.trySend(it)
                handleError(it)
            }
        )
        player?.setRebufferToLive(true)
        loadStream()
    }
    private fun loadStream() {
        viewModelScope.launch {
            try {
                player?.load(HLS.toUri())
                player?.play()
            } catch (e: Exception) {
                handleError(e)
            }
        }
    }
    private fun handlePlayerStateChange(state: Player.State) {
        when (state) {
            Player.State.BUFFERING -> {
                _isLoading.value = false
                _playerState.value = PlayerState.Buffering
                _onBuffering.trySend(true)
            }
            Player.State.READY -> {
                _isLoading.value = false
                _playerState.value = PlayerState.Ready
                player?.qualities?.firstOrNull { it.name == "MAX_QUALITY" }?.let { quality ->
                    player?.setAutoMaxQuality(quality)
                }
            }
            Player.State.PLAYING -> {
                _isLoading.value = false
                _playerState.value = PlayerState.Playing
                _onBuffering.trySend(false)
            }
            Player.State.ENDED -> {
                _isLoading.value = false
                _playerState.value = PlayerState.Ended
                _onBuffering.trySend(false)
            }
            else -> {
                _playerState.value = PlayerState.Idle
            }
        }
    }
    fun togglePlayPause() {
        player?.let { player ->
            if (player.state == Player.State.PLAYING) {
                player.pause()
                _playerState.value = PlayerState.Paused
            } else {
                player.play()
                _playerState.value = PlayerState.Playing
            }
        }
    }
    fun retry() {
        _errorState.value = null
        initPlayer()
    }
    fun shouldAutoHideControls(): Boolean {
        return _playerState.value == PlayerState.Playing && _errorState.value == null
    }
    private fun handleError(error: Throwable) {
        Timber.e(error, "Player error occurred")
        _isLoading.value = false
        _playerState.value = PlayerState.Error
        _errorState.value = error.message ?: "Unknown playback error"
    }
    fun release() {
        Timber.d("Releasing player")
        player?.removeListener(playerListener)
        player?.release()
        player = null
    }
}
