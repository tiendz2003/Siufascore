package com.jerry.ronaldo.siufascore.presentation.livestream

import com.amazonaws.ivs.player.Player
import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.data.model.LiveComment
import com.jerry.ronaldo.siufascore.utils.PlayerState
import com.jerry.ronaldo.siufascore.utils.StreamQuality

data class IVSPlayerViewUiState(
    val playerState: PlayerState = PlayerState.Loading,
    val isLoading: Boolean = false,
    val isControlsVisible: Boolean = true,
    val isFullscreen: Boolean = false,
    val matchInfo :String ?= null,
    val errorMessage: String? = null,
    val isQualitySelectorVisible:Boolean = false,
    val matchId: Int? = null,
    val playbackUrl: String? = null,
    val currentUserImg : String? = null,
    val shouldAutoHideControls: Boolean = false,
    val comments: List<LiveComment> = emptyList(),
    val isLoadingCmt: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isPosting: Boolean = false,
    val hasMoreComments: Boolean = true,
    val lastCommentKey: String? = null,
    val availableQualities: List<StreamQuality> = emptyList(),
    val currentQuality: StreamQuality? = null,
):ViewState

sealed class IVSPlayerIntent:Intent {
    data class LoadComments(val matchId: String) : IVSPlayerIntent()
    data class LoadMoreComments(val matchId: String) : IVSPlayerIntent()
    data class PostComment(val matchId: String, val commentText: String) : IVSPlayerIntent()
    data object RefreshComments : IVSPlayerIntent()
    data object LoadStream : IVSPlayerIntent()
    data object PlayPause : IVSPlayerIntent()
    data object Retry : IVSPlayerIntent()
    data object ToggleFullscreen : IVSPlayerIntent()
    data object ShowControls : IVSPlayerIntent()
    data object HideControls : IVSPlayerIntent()
    data object ToggleControlsVisibility : IVSPlayerIntent()
    data class SetMatchId(val matchId: Int) : IVSPlayerIntent()
    data class PlayerReady(val player: Player) : IVSPlayerIntent()
    data class SelectQuality(val qualityOption: StreamQuality) : IVSPlayerIntent()


}
sealed class IVSPlayerEvent :SingleEvent {
    data object EnterFullscreen : IVSPlayerEvent()
    data object ExitFullscreen : IVSPlayerEvent()
    data object StartControlsTimer : IVSPlayerEvent()
    data object StopControlsTimer : IVSPlayerEvent()
    data class ShowError(val message: String) : IVSPlayerEvent()
    data object ScrollToBottom : IVSPlayerEvent()
    data object CommentPostedSuccessfully : IVSPlayerEvent()
}