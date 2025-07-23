package com.jerry.ronaldo.siufascore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class StreamApiResponse(
    val success: Boolean,
    val message: String,
    val data: StreamResponse?,
)
@Serializable
data class StreamResponse(
    val matchId: Long,
    val apiMatchId: Long,
    val matchInfo: String,
    val league: String,
    val status: String,
    val venue: String,
    val hlsUrl: String,
    val streamType: String,
    val isLive: Boolean,
    val quality: String,
)
@Serializable
data class LiveMatch(
    val matchId: Long,
    val apiMatchId: Long,
    val homeTeam: String,
    val awayTeam: String,
    val league: String,
    val matchDate: String,
    val status: String,
    val venue: String,
    val hasStream: Boolean,
    val isLive: Boolean
)