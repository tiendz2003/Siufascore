package com.jerry.ronaldo.siufascore.utils

import com.amazonaws.ivs.player.Quality

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
    val quality: Quality? = null,
    val displayName: String,
    val isSelected: Boolean = false,
    val isAutoQuality: Boolean = false
) {
    companion object {
        fun fromQuality(quality: Quality, isSelected: Boolean = false): StreamQuality {
            val displayName = when {
                quality.name.contains("audio", ignoreCase = true) -> "Audio Only"
                quality.height > 0 -> "${quality.height}p"
                quality.bitrate > 0 -> "${quality.bitrate / 1000}kbps"
                else -> quality.name
            }

            return StreamQuality(
                quality = quality,
                displayName = displayName,
                isSelected = isSelected,
            )
        }

        fun createAutoQuality(isSelected: Boolean = true): StreamQuality {
            return StreamQuality(
                quality = null,
                displayName = "Auto",
                isSelected = isSelected,
                isAutoQuality = true
            )
        }

    }

    fun getQualityDetails(): String {
        return if (isAutoQuality) {
            "Adaptive quality selection"
        } else {
            quality?.let { q ->
                buildString {
                    if (q.bitrate > 0) {
                        append("${q.bitrate / 1000}kbps")
                    }
                    if (q.framerate > 0) {
                        if (isNotEmpty()) append(" • ")
                        append("${q.framerate.toInt()}fps")
                    }
                    if (q.width > 0 && q.height > 0) {
                        if (isNotEmpty()) append(" • ")
                        append("${q.width}×${q.height}")
                    }
                }
            } ?: ""
        }
    }

}
