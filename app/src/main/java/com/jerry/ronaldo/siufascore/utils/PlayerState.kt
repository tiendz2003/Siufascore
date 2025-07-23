package com.jerry.ronaldo.siufascore.utils

sealed class PlayerState {
    data object Idle : PlayerState()
    data object Loading : PlayerState()
    data object Ready : PlayerState()
    data object Playing : PlayerState()
    data object Paused : PlayerState()
    data object Ended : PlayerState()
    data object Buffering : PlayerState()
    data object Error : PlayerState()
}
sealed class PlayerError : Exception() {
    data object NetworkError : PlayerError()
    data object InvalidUrl : PlayerError()
    data object PlayerInitializationError : PlayerError()
    data class UnknownError(val throwable: Throwable) : PlayerError()
}
data class StreamQuality(
    val name: String,
    val width: Int,
    val height: Int,
    val bitrate: Long,
    val framerate: Float
)
