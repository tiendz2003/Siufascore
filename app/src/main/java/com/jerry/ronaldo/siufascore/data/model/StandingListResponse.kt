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
//
@Keep
@Serializable
data class StandingsResponse(
    @SerialName("get")
    val get: String,
    @SerialName("parameters")
    val parameters: StandingsParameters,
    @SerialName("response")
    val response: List<StandingsDataResponse>
) {
    @Keep
    @Serializable
    data class StandingsParameters(
        @SerialName("league")
        val league: String,
        @SerialName("season")
        val season: String
    )

    @Keep
    @Serializable
    data class StandingsDataResponse(
        @SerialName("league")
        val league: StandingsLeague
    ) {
        @Keep
        @Serializable
        data class StandingsLeague(
            @SerialName("id")
            val id: Int,
            @SerialName("name")
            val name: String,
            @SerialName("country")
            val country: String,
            @SerialName("logo")
            val logo: String?,
            @SerialName("flag")
            val flag: String?,
            @SerialName("season")
            val season: Int,
            @SerialName("standings")
            val standings: List<List<StandingTeam>>
        )

        @Keep
        @Serializable
        data class StandingTeam(
            @SerialName("rank")
            val rank: Int,
            @SerialName("team")
            val team: StandingTeamInfo,
            @SerialName("points")
            val points: Int,
            @SerialName("goalsDiff")
            val goalsDiff: Int,
            @SerialName("group")
            val group: String?,
            @SerialName("form")
            val form: String?,
            @SerialName("status")
            val status: String?,
            @SerialName("description")
            val description: String?,
            @SerialName("all")
            val all: StandingStats,
            @SerialName("home")
            val home: StandingStats,
            @SerialName("away")
            val away: StandingStats,
            @SerialName("update")
            val update: String?
        ) {
            @Keep
            @Serializable
            data class StandingTeamInfo(
                @SerialName("id")
                val id: Int,
                @SerialName("name")
                val name: String,
                @SerialName("logo")
                val logo: String?
            )

            @Keep
            @Serializable
            data class StandingStats(
                @SerialName("played")
                val played: Int,
                @SerialName("win")
                val win: Int,
                @SerialName("draw")
                val draw: Int,
                @SerialName("lose")
                val lose: Int,
                @SerialName("goals")
                val goals: StandingGoals
            ) {
                @Keep
                @Serializable
                data class StandingGoals(
                    @SerialName("for")
                    val goalsFor: Int,
                    @SerialName("against")
                    val goalsAgainst: Int
                )
            }
        }
    }
}