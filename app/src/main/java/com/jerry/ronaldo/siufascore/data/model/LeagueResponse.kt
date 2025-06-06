package com.jerry.ronaldo.siufascore.data.model


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class LeagueResponse(
    @SerialName("area")
    val area: Area,
    @SerialName("code")
    val code: String,
    @SerialName("currentSeason")
    val currentSeason: CurrentSeason,
    @SerialName("emblem")
    val emblem: String,
    @SerialName("id")
    val id: Int,
    @SerialName("lastUpdated")
    val lastUpdated: String,
    @SerialName("name")
    val name: String,
    @SerialName("seasons")
    val seasons: List<Season>,
    @SerialName("type")
    val type: String
) {
    @Keep
    @Serializable
    data class Area(
        @SerialName("code")
        val code: String,
        @SerialName("flag")
        val flag: String,
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String
    )

    @Keep
    @Serializable
    data class CurrentSeason(
        @SerialName("currentMatchday")
        val currentMatchday: Int,
        @SerialName("endDate")
        val endDate: String,
        @SerialName("id")
        val id: Int,
        @SerialName("startDate")
        val startDate: String,
        @SerialName("winner")
        val winner: String?
    )

    @Keep
    @Serializable
    data class Season(
        @SerialName("currentMatchday")
        val currentMatchday: Int?,
        @SerialName("endDate")
        val endDate: String,
        @SerialName("id")
        val id: Int,
        @SerialName("startDate")
        val startDate: String,
        @SerialName("winner")
        val winner: WinnerResponse?
    ) {
        @Keep
        @Serializable
        data class WinnerResponse(
            @SerialName("address")
            val address: String,
            @SerialName("clubColors")
            val clubColors: String,
            @SerialName("crest")
            val crest: String,
            @SerialName("founded")
            val founded: Int,
            @SerialName("id")
            val id: Int,
            @SerialName("lastUpdated")
            val lastUpdated: String,
            @SerialName("name")
            val name: String,
            @SerialName("shortName")
            val shortName: String,
            @SerialName("tla")
            val tla: String,
            @SerialName("venue")
            val venue: String,
            @SerialName("website")
            val website: String
        )
    }
}