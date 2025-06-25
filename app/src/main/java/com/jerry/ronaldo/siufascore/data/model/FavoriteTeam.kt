package com.jerry.ronaldo.siufascore.data.model

import androidx.annotation.Keep
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class FavoriteTeam @JvmOverloads constructor(
    val userId: String = "",
    val addedTimestamp:Long = System.currentTimeMillis(),
    val team:TeamInfo = TeamInfo(),
    val league:LeagueInfo = LeagueInfo()
)