package com.jerry.ronaldo.siufascore.data.model


import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class PlayerDetailResponse(
    @SerialName("errors")
    val errors: List<String?>,
    @SerialName("get")
    val `get`: String,
    @SerialName("paging")
    val paging: Paging,
    @SerialName("parameters")
    val parameters: Parameters,
    @SerialName("response")
    val response: List<Response>,
    @SerialName("results")
    val results: Int
) {
    @Keep
    @Serializable
    data class Paging(
        @SerialName("current")
        val current: Int,
        @SerialName("total")
        val total: Int
    )

    @Keep
    @Serializable
    data class Parameters(
        @SerialName("id")
        val id: String,
        @SerialName("season")
        val season: String
    )

    @Keep
    @Serializable
    data class Response(
        @SerialName("player")
        val player: Player,
        @SerialName("statistics")
        val statistics: List<Statistic>
    ) {
        @Keep
        @Serializable
        data class Player(
            @SerialName("age")
            val age: Int,
            @SerialName("birth")
            val birth: Birth,
            @SerialName("firstname")
            val firstname: String,
            @SerialName("height")
            val height: String,
            @SerialName("id")
            val id: Int,
            @SerialName("injured")
            val injured: Boolean,
            @SerialName("lastname")
            val lastname: String,
            @SerialName("name")
            val name: String,
            @SerialName("nationality")
            val nationality: String,
            @SerialName("photo")
            val photo: String,
            @SerialName("weight")
            val weight: String
        ) {
            @Keep
            @Serializable
            data class Birth(
                @SerialName("country")
                val country: String,
                @SerialName("date")
                val date: String,
                @SerialName("place")
                val place: String
            )
        }

        @Keep
        @Serializable
        data class Statistic(
            @SerialName("cards")
            val cards: Cards,
            @SerialName("dribbles")
            val dribbles: Dribbles,
            @SerialName("duels")
            val duels: Duels,
            @SerialName("fouls")
            val fouls: Fouls,
            @SerialName("games")
            val games: Games,
            @SerialName("goals")
            val goals: Goals,
            @SerialName("league")
            val league: League,
            @SerialName("passes")
            val passes: Passes,
            @SerialName("penalty")
            val penalty: Penalty,
            @SerialName("shots")
            val shots: Shots,
            @SerialName("substitutes")
            val substitutes: Substitutes,
            @SerialName("tackles")
            val tackles: Tackles,
            @SerialName("team")
            val team: Team
        ) {
            @Keep
            @Serializable
            data class Cards(
                @SerialName("red")
                val red: Int,
                @SerialName("yellow")
                val yellow: Int,
                @SerialName("yellowred")
                val yellowred: Int
            )

            @Keep
            @Serializable
            data class Dribbles(
                @SerialName("attempts")
                val attempts: Int?,
                @SerialName("past")
                val past: Int?,
                @SerialName("success")
                val success: Int?
            )

            @Keep
            @Serializable
            data class Duels(
                @SerialName("total")
                val total: Int?,
                @SerialName("won")
                val won: Int?
            )

            @Keep
            @Serializable
            data class Fouls(
                @SerialName("committed")
                val committed: Int?,
                @SerialName("drawn")
                val drawn: Int?
            )

            @Keep
            @Serializable
            data class Games(
                @SerialName("appearences")
                val appearences: Int,
                @SerialName("captain")
                val captain: Boolean,
                @SerialName("lineups")
                val lineups: Int,
                @SerialName("minutes")
                val minutes: Int,
                @SerialName("number")
                val number: Int?,
                @SerialName("position")
                val position: String,
                @SerialName("rating")
                val rating: String?
            )

            @Keep
            @Serializable
            data class Goals(
                @SerialName("assists")
                val assists: Int?,
                @SerialName("conceded")
                val conceded: Int?,
                @SerialName("saves")
                val saves: Int?,
                @SerialName("total")
                val total: Int
            )

            @Keep
            @Serializable
            data class League(
                @SerialName("country")
                val country: String?,
                @SerialName("flag")
                val flag: String?,
                @SerialName("id")
                val id: Int?,
                @SerialName("logo")
                val logo: String?,
                @SerialName("name")
                val name: String,
                @SerialName("season")
                val season: Int
            )

            @Keep
            @Serializable
            data class Passes(
                @SerialName("accuracy")
                val accuracy: Int?,
                @SerialName("key")
                val key: Int?,
                @SerialName("total")
                val total: Int?
            )

            @Keep
            @Serializable
            data class Penalty(
                @SerialName("commited")
                val commited: Int?,
                @SerialName("missed")
                val missed: Int?,
                @SerialName("saved")
                val saved: Int?,
                @SerialName("scored")
                val scored: Int?,
                @SerialName("won")
                val won: Int?
            )

            @Keep
            @Serializable
            data class Shots(
                @SerialName("on")
                val on: Int?,
                @SerialName("total")
                val total: Int?
            )

            @Keep
            @Serializable
            data class Substitutes(
                @SerialName("bench")
                val bench: Int,
                @SerialName("in")
                val inX: Int,
                @SerialName("out")
                val `out`: Int
            )

            @Keep
            @Serializable
            data class Tackles(
                @SerialName("blocks")
                val blocks: Int?,
                @SerialName("interceptions")
                val interceptions: Int?,
                @SerialName("total")
                val total: Int?
            )

            @Keep
            @Serializable
            data class Team(
                @SerialName("id")
                val id: Int,
                @SerialName("logo")
                val logo: String,
                @SerialName("name")
                val name: String
            )
        }
    }
}