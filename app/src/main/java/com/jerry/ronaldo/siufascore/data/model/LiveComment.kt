package com.jerry.ronaldo.siufascore.data.model

import androidx.annotation.Keep
import com.google.firebase.database.IgnoreExtraProperties
import kotlinx.serialization.Serializable

@Keep
@Serializable
@IgnoreExtraProperties
data class LiveComment @JvmOverloads constructor (
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val userImage: String = "",
    val comment: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)

data class CommentResult(
    val comments: List<LiveComment> = emptyList(),
    val hasMore: Boolean = false,
    val lastKey: String? = null
)