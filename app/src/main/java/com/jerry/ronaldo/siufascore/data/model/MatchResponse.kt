package com.jerry.ronaldo.siufascore.data.model


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class MatchResponse(
    @SerialName("get")
    val `get`: String,
    @SerialName("parameters")
    val parameters: Parameters,
    @SerialName("response")
    val response: List<Response>,
) {
    @Keep
    @Serializable
    data class Parameters(
        @SerialName("season")
        val season: String
    )

    @Keep
    @Serializable
    data class Response(
        @SerialName("fixture")
        val fixture: Fixture,
        @SerialName("goals")
        val goals: Goals,
        @SerialName("league")
        val league: League,
        @SerialName("score")
        val score: Score,
        @SerialName("teams")
        val teams: Teams
    ) {
        @Keep
        @Serializable
        data class Fixture(
            @SerialName("date")
            val date: String,
            @SerialName("id")
            val id: Int,
            @SerialName("periods")
            val periods: Periods?,
            @SerialName("referee")
            val referee: String?,
            @SerialName("status")
            val status: Status,
            @SerialName("timestamp")
            val timestamp: Int,
            @SerialName("timezone")
            val timezone: String,
            @SerialName("venue")
            val venue: Venue?
        ) {
            @Keep
            @Serializable
            data class Periods(
                @SerialName("first")
                val first: Int?,
                @SerialName("second")
                val second: Int?
            )

            @Keep
            @Serializable
            data class Status(
                @SerialName("elapsed")
                val elapsed: Int?,
                @SerialName("extra")
                val extra: String?,
                @SerialName("long")
                val long: String,
                @SerialName("short")
                val short: String
            )

            @Keep
            @Serializable
            data class Venue(
                @SerialName("city")
                val city: String?,
                @SerialName("id")
                val id: Int?,
                @SerialName("name")
                val name: String?
            )
        }

        @Keep
        @Serializable
        data class Goals(
            @SerialName("away")
            val away: Int?,
            @SerialName("home")
            val home: Int?
        )

        @Keep
        @Serializable
        data class League(
            @SerialName("country")
            val country: String,
            @SerialName("flag")
            val flag: String?,
            @SerialName("id")
            val id: Int,
            @SerialName("logo")
            val logo: String?,
            @SerialName("name")
            val name: String,
            @SerialName("round")
            val round: String,
            @SerialName("season")
            val season: Int,
            @SerialName("standings")
            val standings: Boolean
        )

        @Keep
        @Serializable
        data class Score(
            @SerialName("extratime")
            val extratime: Extratime,
            @SerialName("fulltime")
            val fulltime: Fulltime,
            @SerialName("halftime")
            val halftime: Halftime,
            @SerialName("penalty")
            val penalty: Penalty
        ) {
            @Keep
            @Serializable
            data class Extratime(
                @SerialName("away")
                val away: String?,
                @SerialName("home")
                val home: String?
            )

            @Keep
            @Serializable
            data class Fulltime(
                @SerialName("away")
                val away: Int?,
                @SerialName("home")
                val home: Int?
            )

            @Keep
            @Serializable
            data class Halftime(
                @SerialName("away")
                val away: Int?,
                @SerialName("home")
                val home: Int?
            )

            @Keep
            @Serializable
            data class Penalty(
                @SerialName("away")
                val away: String?,
                @SerialName("home")
                val home: String?
            )
        }

        @Keep
        @Serializable
        data class Teams(
            @SerialName("away")
            val away: Team,
            @SerialName("home")
            val home: Team
        ) {
            @Keep
            @Serializable
            data class Team(
                @SerialName("id")
                val id: Int,
                @SerialName("logo")
                val logo: String?,
                @SerialName("name")
                val name: String,
                @SerialName("winner")
                val winner: Boolean?
            )
        }
    }
}