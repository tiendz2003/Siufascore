package com.jerry.ronaldo.siufascore.presentation.livestream

import com.jerry.ronaldo.siufascore.utils.PlayerState
import com.jerry.ronaldo.siufascore.utils.StreamQuality

data class IVSPlayerUiState(
    val playerState: PlayerState = PlayerState.Idle,
    val streamUrl: String = "",
    val currentPosition: Long = 0L,
    val duration: Long = 0L,
    val availableQualities: List<StreamQuality> = emptyList(),
    val currentQuality: StreamQuality? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)