package com.jerry.ronaldo.siufascore.utils

import android.app.Activity
import android.content.Context
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.annotation.ColorRes
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
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.palette.graphics.Palette
import coil3.imageLoader
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.PlayerSeasonStats
import com.jerry.ronaldo.siufascore.presentation.ui.PremierPurpleDark
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Month
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


fun String.formatDisplayDate(): String {
    val inputFormat = SimpleDateFormat("MM-dd-yyyy", Locale.getDefault())
    val outputFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())

    val date = inputFormat.parse(this) ?: return this
    return outputFormat.format(date)
}

fun String?.extractRoundNumber(): String {
    return this?.split("-")?.last()?.trim() ?: "0"
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

fun Exception.handleException(): Exception {
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

fun Exception.handleAuthException(): AuthException {
    return when (this) {
        is AuthException -> this
        is IOException -> AuthException.NetworkError
        is HttpException -> when (this.code()) {
            401 -> AuthException.InvalidCredentials
            404 -> AuthException.UserNotFound
            409 -> AuthException.EmailAlreadyExists
            in 500..599 -> AuthException.ServerError
            else -> AuthException.Unknown(this.message ?: "Unknown error")
        }

        else -> AuthException.Unknown(this.message ?: "Unknown error")
    }
}

fun List<PlayerSeasonStats>.mapStats(): List<Pair<String, String>> {
    // Lấy bản ghi đầu tiên hoặc gộp dữ liệu nếu cần
    val stat = this.firstOrNull() ?: return emptyList()
    return listOfNotNull(
        stat.games?.appearences?.let { "Appearances" to it.toString() },
        stat.goals?.total?.let { "Goals" to it.toString() },
        stat.goals?.assists?.let { "Assists" to it.toString() },
        stat.rating?.let {
            "Rating" to String.format(Locale.US, "%.1f", it)
        },
        stat.shots?.accuracy?.let {
            "Shot Accuracy" to String.format(Locale.US, "%.1f%%", it)
        },
        stat.duels?.winPercentage?.let {
            "Duel Win %" to String.format(Locale.US, "%.1f%%", it)
        },
        stat.cards?.yellow?.let { "Yellow Cards" to it.toString() },
        stat.games?.minutes?.let { "Minutes Played" to it.toString() }
    )
}

fun String.extractYear(): Int {
    val regex = "\\d{4}".toRegex()  // Tìm chuỗi 4 chữ số liên tiếp
    val match = regex.find(this)  // Tìm match đầu tiên
    return match?.value?.toIntOrNull() ?: 0  // Convert sang Int, fallback 0 nếu null
}

fun Long.formatTimestamp(format: String = "dd/MM/yyyy HH:mm"): String {
    // Sử dụng try-catch để xử lý các trường hợp timestamp không hợp lệ một cách an toàn
    return try {
        // Tạo một đối tượng Date từ timestamp (this)
        val date = Date(this)
        // Tạo một đối tượng định dạng với kiểu và ngôn ngữ mặc định của thiết bị
        val simpleDateFormat = SimpleDateFormat(format, Locale.getDefault())
        // Trả về chuỗi đã được định dạng
        simpleDateFormat.format(date)
    } catch (e: Exception) {
        // In ra lỗi để gỡ rối nếu cần
        e.printStackTrace()
        // Trả về chuỗi rỗng nếu có vấn đề xảy ra
        ""
    }
}

fun Long.toTimeAgo(): String {
    val now = System.currentTimeMillis()
    // Đảm bảo timestamp không phải ở tương lai
    if (this > now || this <= 0) {
        return "thời gian không hợp lệ"
    }

    val diffInMillis = now - this

    // Chuyển đổi sang các đơn vị thời gian khác nhau
    val seconds = TimeUnit.MILLISECONDS.toSeconds(diffInMillis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffInMillis)
    val hours = TimeUnit.MILLISECONDS.toHours(diffInMillis)
    val days = TimeUnit.MILLISECONDS.toDays(diffInMillis)

    return when {
        seconds < 60 -> "vừa xong"
        minutes < 60 -> "$minutes phút trước"
        hours < 24 -> "$hours giờ trước"
        days < 7 -> "$days ngày trước"
        // Nếu lâu hơn một tuần, hiển thị ngày tháng cụ thể
        else -> this.formatTimestamp("dd/MM/yyyy")
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

fun getCurrentSeason(): Int {
    val currentDate = LocalDate.now()
    return if (currentDate.month.value >= Month.AUGUST.value) {
        currentDate.year
    } else {
        currentDate.year - 1
    }
}

fun String.getLeagueIdFromCountry(): Int? {
    return when (this.lowercase()) {  // Case-insensitive để an toàn
        "england" -> 39     // Premier League
        "spain" -> 140       // La Liga
        "italy" -> 135       // Serie A
        "germany" -> 78      // Bundesliga
        "france" -> 61       // Ligue 1
        "portugal" -> 94     // Primeira Liga
        "netherlands" -> 88  // Eredivisie
        "belgium" -> 144     // Pro League
        "scotland" -> 179    // Premiership
        "turkey" -> 203      // Süper Lig
        "switzerland" -> 207 // Super League
        "austria" -> 218     // Bundesliga
        "russia" -> 235      // Premier League
        else -> null
    }
}

suspend fun Context.extractAllColors(
    imageUrl: String,
): ExtractedColors {
    return withContext(Dispatchers.IO) {
        try {
            val request = ImageRequest.Builder(this@extractAllColors).data(imageUrl)
                .size(300, 300)
                .allowHardware(false)
                .build()

            val drawable = this@extractAllColors.imageLoader.execute(request).image
            val bitmap = drawable?.toBitmap()
            bitmap?.let { bitmap ->
                val palette = Palette.from(bitmap).generate()
                ExtractedColors(
                    dominant = palette.dominantSwatch?.rgb?.let { Color(it) } ?: PremierPurpleDark,
                    vibrant = palette.vibrantSwatch?.rgb?.let { Color(it) } ?: PremierPurpleDark,
                    muted = palette.mutedSwatch?.rgb?.let { Color(it) } ?: PremierPurpleDark,
                    lightVibrant = palette.lightVibrantSwatch?.rgb?.let { Color(it) }
                        ?: PremierPurpleDark,
                    onVibrant = palette.vibrantSwatch?.titleTextColor?.let { Color(it) }
                        ?: Color.White
                )
            } ?: ExtractedColors()
        } catch (e: Exception) {
            Timber.tag("extractAllColors").e("${e.message}")
            ExtractedColors()
        }
    }
}

inline fun databaseListener(
    crossinline onChildAdded: (snapshot: DataSnapshot, previousChildName: String?) -> Unit = { _, _ -> },
    crossinline onChildChanged: (snapshot: DataSnapshot, previousChildName: String?) -> Unit = { _, _ -> },
    crossinline onChildRemoved: (snapshot: DataSnapshot) -> Unit = { _ -> },
    crossinline onChildMoved: (snapshot: DataSnapshot, previousChildName: String?) -> Unit = { _, _ -> },
    crossinline onCancelled: (error: DatabaseError) -> Unit = { _ -> }
): ChildEventListener = object : ChildEventListener {
    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        onChildAdded(snapshot, previousChildName)
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
        onChildChanged(snapshot, previousChildName)
    }

    override fun onChildRemoved(snapshot: DataSnapshot) {
        onChildRemoved(snapshot)
    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
        onChildMoved(snapshot, previousChildName)
    }

    override fun onCancelled(error: DatabaseError) {
        onCancelled(error)
    }

}

inline fun Query.setListener(
    crossinline onChildAdded: (snapshot: DataSnapshot, previousChildName: String?) -> Unit = { _, _ -> },
    crossinline onChildChanged: (snapshot: DataSnapshot, previousChildName: String?) -> Unit = { _, _ -> },
    crossinline onChildRemoved: (snapshot: DataSnapshot) -> Unit = { _ -> },
    crossinline onChildMoved: (snapshot: DataSnapshot, previousChildName: String?) -> Unit = { _, _ -> },
    crossinline onCancelled: (error: DatabaseError) -> Unit = { _ -> }
): ChildEventListener {
    val listener = databaseListener(
        onChildAdded, onChildChanged, onChildRemoved, onChildMoved, onCancelled
    )
    this.addChildEventListener(listener)
    return listener
}

fun Query.initDatabaseListener(
    onChildAdded: (snapshot: DataSnapshot, previousChildName: String?) -> Unit = { _, _ -> },
    onChildChanged: (snapshot: DataSnapshot, previousChildName: String?) -> Unit = { _, _ -> },
    onChildRemoved: (snapshot: DataSnapshot) -> Unit = { _ -> },
    onCancelled: (error: DatabaseError) -> Unit = { _ -> }
) = setListener(
    onChildAdded = onChildAdded,
    onChildChanged = onChildChanged,
    onChildRemoved = onChildRemoved,
    onCancelled = onCancelled
)

fun Activity.showSnackbar(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
    findViewById<View>(android.R.id.content).showSnackbar(
        message = message,
        duration = duration,
        backgroundColorRes = R.color.purple,
        textColorRes = android.R.color.white
    )
}

fun View.showSnackbar(
    message: String,
    duration: Int,
    @ColorRes backgroundColorRes: Int,
    @ColorRes textColorRes: Int
) {
    Snackbar.make(this, message, duration).apply {
        setBackgroundTint(ContextCompat.getColor(context, backgroundColorRes))
        setTextColor(ContextCompat.getColor(context, textColorRes))
        show()
    }
}

fun Fragment.handleBack(action: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            action()
        }
    })

}