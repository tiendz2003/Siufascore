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
import retrofit2.HttpException
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale


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
fun String.getPositionAbbreviation(): String {
    return when (this) {
        "Goalkeeper" -> "GK"
        "Left-Back" -> "LB"
        "Centre-Back" -> "CB"
        "Right-Back" -> "RB"
        "Defensive Midfield" -> "DM"
        "Central Midfield" -> "CM"
        "Attacking Midfield" -> "AM"
        "Left Winger" -> "LW"
        "Right Winger" -> "RW"
        "Centre-Forward" -> "CF"
        else -> this.take(2).uppercase()
    }
}
fun Throwable.handleException(): Exception {
    return when (this) {
        is HttpException -> {
            when (this.code()) {
                401 -> Exception("Không có quyền truy cập")
                404 -> Exception("Không tìm thấy dữ liệu")
                500 -> Exception("Lỗi sever")
                else -> Exception("API Error: ${this.code()}")
            }
        }

        is IOException -> Exception("Lỗi mạng:${this.localizedMessage}")
        else -> Exception("Lỗi không rõ:${this.localizedMessage}")
    }
}
fun String.formatYouTubeTime(): String {
    return try {
        // Parse chuỗi ISO 8601
        val publishedTime = ZonedDateTime.parse(this, DateTimeFormatter.ISO_DATE_TIME)
        val now = ZonedDateTime.now()

        // Tính khoảng cách thời gian
        val seconds = ChronoUnit.SECONDS.between(publishedTime, now)
        val minutes = ChronoUnit.MINUTES.between(publishedTime, now)
        val hours = ChronoUnit.HOURS.between(publishedTime, now)
        val days = ChronoUnit.DAYS.between(publishedTime, now)
        val weeks = days / 7
        val months = ChronoUnit.MONTHS.between(publishedTime, now)
        val years = ChronoUnit.YEARS.between(publishedTime, now)

        when {
            seconds < 60 -> "vừa xong"
            minutes < 60 -> "$minutes phút trước"
            hours < 24 -> "$hours giờ trước"
            days < 7 -> "$days ngày trước"
            weeks < 4 -> "$weeks tuần trước"
            months < 12 -> "$months tháng trước"
            else -> {
                publishedTime.format(DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.US))
            }
        }
    } catch (e: Exception) {
        "Không rõ ngày đăng"
    }
}
fun String?.formatViewCount(): String {
    val viewCount = this?.toLongOrNull() ?: return "0 lượt xem"
    return when {
        viewCount >= 1_000_000 -> "%.1fM lượt xem".format(viewCount / 1_000_000.0)
        viewCount >= 1_000 -> "%.1fK lượt xem".format(viewCount / 1_000.0)
        else -> "$viewCount lượt xem"
    }
}
fun String?.extractDescription(): String? {
    return this?.let {
        if (it.length > 200) it.substring(0, 200) + "..." else it
    }
}