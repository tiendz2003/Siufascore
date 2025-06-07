package com.jerry.ronaldo.siufascore.utils

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone



fun String.formatDisplayDate(): String {
    val inputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

    val date = inputFormat.parse(this) ?: return this
    return outputFormat.format(date)
}

fun String.extractDateFromUtc(): String {
    // Trích xuất phần ngày (YYYY-MM-DD) từ chuỗi utcDate
    return this.split("T")[0]
}

inline fun <T, R> Resource<T>.map(transform: (T) -> R): Resource<R> {
    return when (this) {
        is Resource.Success -> {
            try {
                Resource.Success(transform(data))
            } catch (e: Exception) {
                Resource.Error(e)
            }
        }

        is Resource.Error -> Resource.Error(exception)
        is Resource.Loading -> Resource.Loading
    }
}

inline fun <T> Resource<T>.fold(
    onSuccess: (T) -> Unit = {},
    onError: (Exception) -> Unit = {},
    onLoading: () -> Unit = {}
) {
    when (this) {
        is Resource.Success -> onSuccess(data)
        is Resource.Error -> onError(exception)
        is Resource.Loading -> onLoading()
    }
}

fun whoIsWinner(homeScore: Int?, awayScore: Int?): WinnerSide {
    if (homeScore == null || awayScore == null) return WinnerSide.NONE
    return when {
        homeScore > awayScore -> WinnerSide.HOME
        homeScore < awayScore -> WinnerSide.AWAY
        else -> WinnerSide.DRAW
    }
}

@Composable
fun Modifier.shimmerEffect(): Modifier {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val alpha by transition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    return this.alpha(alpha)
}