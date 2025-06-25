package com.jerry.ronaldo.siufascore.presentation.highlight

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.VideoItem

data class HighLightUiState(
    val playlistId: String? = null,
    val selectedLeague: String? = null,
    val videoId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val infoVideo: VideoItem? = null
) : ViewState

sealed class HighLightIntent : Intent {
    data class LoadHighlightByLeague(val playlistId: String) : HighLightIntent()
    data class SetPlaylistId(val playlistId: String) : HighLightIntent()
    data object RefreshData : HighLightIntent()
    data class SetVideoId(val videoId: String) : HighLightIntent()

}

sealed class HighLightEffect : SingleEvent {
    data class ShowError(val error: String) : HighLightEffect()
    data class NavigateToDetailScreen(val matchId: Int) : HighLightEffect()
}

data class DetailHighLightUiState(
    val playlistId: String? = null,
    val selectedLeague: String? = null,
    val videoId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val infoVideo: VideoItem? = null
) : ViewState

sealed class DetailHighLightEffect : SingleEvent {
    data class ShowError(val error: String) : DetailHighLightEffect()
    data object NavigateBack : DetailHighLightEffect()
}

sealed class DetailHighLightIntent : Intent {
    data class LoadHighlightByLeague(val playlistId: String) : DetailHighLightIntent()
    data class SetPlaylistId(val playlistId: String) : DetailHighLightIntent()
    data object RefreshData : DetailHighLightIntent()
    data class SetVideoId(val videoId: String) : DetailHighLightIntent()
    data object ClearVideoId : DetailHighLightIntent()
    data object ToggleComments : DetailHighLightIntent()

}
