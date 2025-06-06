package com.jerry.ronaldo.siufascore.data.model


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class StandingListResponse(
    @SerialName("area")
    val area: AreaResponse,
    @SerialName("competition")
    val competition: CompetitionResponse,
    @SerialName("filters")
    val filters: FiltersResponse,
    @SerialName("season")
    val season: SeasonResponse,
    @SerialName("standings")
    val standings: List<StandingResponse>
) {
    @Keep
    @Serializable
    data class AreaResponse(
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
    data class CompetitionResponse(
        @SerialName("code")
        val code: String,
        @SerialName("emblem")
        val emblem: String,
        @SerialName("id")
        val id: Int,
        @SerialName("name")
        val name: String,
        @SerialName("type")
        val type: String
    )

    @Keep
    @Serializable
    data class FiltersResponse(
        @SerialName("season")
        val season: String
    )

    @Keep
    @Serializable
    data class SeasonResponse(
        @SerialName("currentMatchday")
        val currentMatchday: Int,
        @SerialName("endDate")
        val endDate: String,
        @SerialName("id")
        val id: Int,
        @SerialName("startDate")
        val startDate: String,
    )

    @Keep
    @Serializable
    data class StandingResponse(
        @SerialName("stage")
        val stage: String,
        @SerialName("table")
        val table: List<TableResponse>,
        @SerialName("type")
        val type: String
    ) {
        @Keep
        @Serializable
        data class TableResponse(
            @SerialName("draw")
            val draw: Int,
            @SerialName("goalDifference")
            val goalDifference: Int,
            @SerialName("goalsAgainst")
            val goalsAgainst: Int,
            @SerialName("goalsFor")
            val goalsFor: Int,
            @SerialName("lost")
            val lost: Int,
            @SerialName("playedGames")
            val playedGames: Int,
            @SerialName("points")
            val points: Int,
            @SerialName("position")
            val position: Int,
            @SerialName("team")
            val team: TeamResponse,
            @SerialName("won")
            val won: Int
        ) {
            @Keep
            @Serializable
            data class TeamResponse(
                @SerialName("crest")
                val crest: String,
                @SerialName("id")
                val id: Int,
                @SerialName("name")
                val name: String,
                @SerialName("shortName")
                val shortName: String,
                @SerialName("tla")
                val tla: String
            )
        }
    }
}