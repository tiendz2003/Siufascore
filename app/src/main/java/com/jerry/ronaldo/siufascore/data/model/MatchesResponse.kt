package com.jerry.ronaldo.siufascore.data.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep

@Keep
@Serializable
data class MatchesResponse(
    @SerialName("competition")
    val competition: CompetitionResponse,
    @SerialName("filters")
    val filters: FiltersResponse,
    @SerialName("matches")
    val matches: List<MatcheResponse>,
    @SerialName("resultSet")
    val resultSet: ResultSetResponse
) {
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
    data class MatcheResponse(
        @SerialName("area")
        val area: AreaResponse,
        @SerialName("awayTeam")
        val awayTeam: TeamResponse,
        @SerialName("competition")
        val competition: CompetitionResponse,
        @SerialName("homeTeam")
        val homeTeam: TeamResponse,
        @SerialName("id")
        val id: Int,
        @SerialName("lastUpdated")
        val lastUpdated: String,
        @SerialName("matchday")
        val matchday: Int,
        @SerialName("odds")
        val odds: OddsResponse,
        @SerialName("referees")
        val referees: List<RefereeResponse>,
        @SerialName("score")
        val score: ScoreResponse,
        @SerialName("season")
        val season: SeasonResponse,
        @SerialName("stage")
        val stage: String,
        @SerialName("status")
        val status: String,
        @SerialName("utcDate")
        val utcDate: String
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
        data class OddsResponse(
            @SerialName("msg")
            val msg: String
        )

        @Keep
        @Serializable
        data class RefereeResponse(
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
            @SerialName("nationality")
            val nationality: String,
            @SerialName("type")
            val type: String
        )

        @Keep
        @Serializable
        data class ScoreResponse(
            @SerialName("duration")
            val duration: String,
            @SerialName("extraTime")
            val extraTime: ExtraTimeResponse? =null,
            @SerialName("fullTime")
            val fullTime: ScoreDetailResponse,
            @SerialName("halfTime")
            val halfTime: ScoreDetailResponse,
            @SerialName("penalties")
            val penalties: PenaltiesResponse?=null,
            @SerialName("regularTime")
            val regularTime: RegularTimeResponse?=null,
            @SerialName("winner")
            val winner: String?
        ) {
            @Keep
            @Serializable
            data class ExtraTimeResponse(
                @SerialName("away")
                val away: Int,
                @SerialName("home")
                val home: Int
            )

            @Keep
            @Serializable
            data class ScoreDetailResponse(
                @SerialName("away")
                val away: Int?,
                @SerialName("home")
                val home: Int?
            )


            @Keep
            @Serializable
            data class PenaltiesResponse(
                @SerialName("away")
                val away: Int,
                @SerialName("home")
                val home: Int
            )

            @Keep
            @Serializable
            data class RegularTimeResponse(
                @SerialName("away")
                val away: Int,
                @SerialName("home")
                val home: Int
            )
        }

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
    }

    @Keep
    @Serializable
    data class ResultSetResponse(
        @SerialName("count")
        val count: Int,
        @SerialName("first")
        val first: String,
        @SerialName("last")
        val last: String,
        @SerialName("played")
        val played: Int
    )
}