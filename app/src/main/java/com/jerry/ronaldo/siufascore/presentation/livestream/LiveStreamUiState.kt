package com.jerry.ronaldo.siufascore.presentation.livestream

import com.jerry.ronaldo.siufascore.base.Intent
import com.jerry.ronaldo.siufascore.base.SingleEvent
import com.jerry.ronaldo.siufascore.base.ViewState
import com.jerry.ronaldo.siufascore.domain.model.Match

data class LiveStreamUiState(
    val isLoading:Boolean = false,
    val errorMessage:String? = null,
    val matches: List<Match> = emptyList(),
):ViewState

sealed class LiveStreamIntent:Intent{
    data object SetSelectedLeague:LiveStreamIntent()
    data class SetSelectedMatch(val matchId:Int):LiveStreamIntent()
    data object RefreshData:LiveStreamIntent()
}
sealed class LiveStreamEffect:SingleEvent {
    data class ShowError(val errorMessage: String) : LiveStreamEffect()
    data class NavigateToIvsPlayerScreen(val matchId: Int) : LiveStreamEffect()
    data object NavigateBack : LiveStreamEffect()
}