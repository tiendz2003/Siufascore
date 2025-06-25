// Fixture Detail Data Models
package com.jerry.ronaldo.siufascore.data.model

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class DetailMatchResponse(
    @SerialName("get")
    val get: String,
    @SerialName("parameters")
    val parameters: FixtureDetailParameters,
    @SerialName("response")
    val response: List<FixtureDetailData>
) {
    @Keep
    @Serializable
    data class FixtureDetailParameters(
        @SerialName("id")
        val id: String
    )

    @Keep
    @Serializable
    data class FixtureDetailData(
        @SerialName("fixture")
        val fixture: Fixture,
        @SerialName("league")
        val league: League,
        @SerialName("teams")
        val teams: Teams,
        @SerialName("goals")
        val goals: Goals,
        @SerialName("score")
        val score: Score,
        @SerialName("events")
        val events: List<Event> = emptyList(),
        @SerialName("lineups")
        val lineups: List<Lineup> = emptyList(),
        @SerialName("statistics")
        val statistics: List<TeamStatistics> = emptyList(),
        @SerialName("players")
        val players: List<TeamPlayers> = emptyList()
    ) {
        // Reuse existing Fixture, League, Teams, Goals, Score from Matchv2Response
        @Keep
        @Serializable
        data class Fixture(
            @SerialName("id")
            val id: Int,
            @SerialName("referee")
            val referee: String?,
            @SerialName("timezone")
            val timezone: String,
            @SerialName("date")
            val date: String,
            @SerialName("timestamp")
            val timestamp: Int,
            @SerialName("periods")
            val periods: Periods?,
            @SerialName("venue")
            val venue: Venue?,
            @SerialName("status")
            val status: Status
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
            data class Venue(
                @SerialName("id")
                val id: Int?,
                @SerialName("name")
                val name: String?,
                @SerialName("city")
                val city: String?
            )

            @Keep
            @Serializable
            data class Status(
                @SerialName("long")
                val long: String,
                @SerialName("short")
                val short: String,
                @SerialName("elapsed")
                val elapsed: Int?,
                @SerialName("extra")
                val extra: String?
            )
        }

        @Keep
        @Serializable
        data class League(
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
            @SerialName("round")
            val round: String
        )

        @Keep
        @Serializable
        data class Teams(
            @SerialName("home")
            val home: Team,
            @SerialName("away")
            val away: Team
        ) {
            @Keep
            @Serializable
            data class Team(
                @SerialName("id")
                val id: Int,
                @SerialName("name")
                val name: String,
                @SerialName("logo")
                val logo: String?,
                @SerialName("winner")
                val winner: Boolean?
            )
        }

        @Keep
        @Serializable
        data class Goals(
            @SerialName("home")
            val home: Int?,
            @SerialName("away")
            val away: Int?
        )

        @Keep
        @Serializable
        data class Score(
            @SerialName("halftime")
            val halftime: ScoreDetail,
            @SerialName("fulltime")
            val fulltime: ScoreDetail,
            @SerialName("extratime")
            val extratime: ScoreDetail,
            @SerialName("penalty")
            val penalty: ScoreDetail
        ) {
            @Keep
            @Serializable
            data class ScoreDetail(
                @SerialName("home")
                val home: Int?,
                @SerialName("away")
                val away: Int?
            )
        }

        // ==================== EVENTS ====================
        @Keep
        @Serializable
        data class Event(
            @SerialName("time")
            val time: EventTime,
            @SerialName("team")
            val team: EventTeam,
            @SerialName("player")
            val player: EventPlayer,
            @SerialName("assist")
            val assist: EventPlayer?,
            @SerialName("type")
            val type: String,
            @SerialName("detail")
            val detail: String,
            @SerialName("comments")
            val comments: String?
        ) {
            @Keep
            @Serializable
            data class EventTime(
                @SerialName("elapsed")
                val elapsed: Int,
                @SerialName("extra")
                val extra: Int?
            )

            @Keep
            @Serializable
            data class EventTeam(
                @SerialName("id")
                val id: Int,
                @SerialName("name")
                val name: String,
                @SerialName("logo")
                val logo: String?
            )

            @Keep
            @Serializable
            data class EventPlayer(
                @SerialName("id")
                val id: Int?,
                @SerialName("name")
                val name: String?
            )
        }

        // ==================== LINEUPS ====================
        @Keep
        @Serializable
        data class Lineup(
            @SerialName("team")
            val team: LineupTeam,
            @SerialName("coach")
            val coach: Coach,
            @SerialName("formation")
            val formation: String?,
            @SerialName("startXI")
            val startXI: List<LineupPlayer> = emptyList(),
            @SerialName("substitutes")
            val substitutes: List<LineupPlayer> = emptyList()
        ) {
            @Keep
            @Serializable
            data class LineupTeam(
                @SerialName("id")
                val id: Int,
                @SerialName("name")
                val name: String,
                @SerialName("logo")
                val logo: String?,
                @SerialName("colors")
                val colors: TeamColors?
            ) {
                @Keep
                @Serializable
                data class TeamColors(
                    @SerialName("player")
                    val player: ColorSet?,
                    @SerialName("goalkeeper")
                    val goalkeeper: ColorSet?
                ) {
                    @Keep
                    @Serializable
                    data class ColorSet(
                        @SerialName("primary")
                        val primary: String?,
                        @SerialName("number")
                        val number: String?,
                        @SerialName("border")
                        val border: String?
                    )
                }
            }

            @Keep
            @Serializable
            data class Coach(
                @SerialName("id")
                val id: Int?,
                @SerialName("name")
                val name: String?,
                @SerialName("photo")
                val photo: String?
            )

            @Keep
            @Serializable
            data class LineupPlayer(
                @SerialName("player")
                val player: Player,
                @SerialName("statistics")
                val statistics: PlayerStatistics? = null
            ) {
                @Keep
                @Serializable
                data class Player(
                    @SerialName("id")
                    val id: Int,
                    @SerialName("name")
                    val name: String,
                    @SerialName("number")
                    val number: Int?,
                    @SerialName("pos")
                    val pos: String?,
                    @SerialName("grid")
                    val grid: String?
                )

                @Keep
                @Serializable
                data class PlayerStatistics(
                    @SerialName("minutes_played")
                    val minutesPlayed: Int?
                )
            }
        }

        // ==================== STATISTICS ====================
        @Keep
        @Serializable
        data class TeamStatistics(
            @SerialName("team")
            val team: StatTeam,
            @SerialName("statistics")
            val statistics: List<Statistic>
        ) {
            @Keep
            @Serializable
            data class StatTeam(
                @SerialName("id")
                val id: Int,
                @SerialName("name")
                val name: String,
                @SerialName("logo")
                val logo: String?
            )

            @Keep
            @Serializable
            data class Statistic(
                @SerialName("type")
                val type: String,
                @SerialName("value")
                val value: String?
            )
        }

        // ==================== PLAYERS ====================
        @Keep
        @Serializable
        data class TeamPlayers(
            @SerialName("team")
            val team: PlayerTeam,
            @SerialName("players")
            val players: List<PlayerDetail>
        ) {
            @Keep
            @Serializable
            data class PlayerTeam(
                @SerialName("id")
                val id: Int,
                @SerialName("name")
                val name: String,
                @SerialName("logo")
                val logo: String?,
                @SerialName("update")
                val update: String?
            )

            @Keep
            @Serializable
            data class PlayerDetail(
                @SerialName("player")
                val player: PlayerInfo,
                @SerialName("statistics")
                val statistics: List<PlayerMatchStatistics>
            ) {
                @Keep
                @Serializable
                data class PlayerInfo(
                    @SerialName("id")
                    val id: Int,
                    @SerialName("name")
                    val name: String,
                    @SerialName("photo")
                    val photo: String?
                )

                @Keep
                @Serializable
                data class PlayerMatchStatistics(
                    @SerialName("games")
                    val games: GameStats?,
                    @SerialName("offsides")
                    val offsides: Int?,
                    @SerialName("shots")
                    val shots: ShotStats?,
                    @SerialName("goals")
                    val goals: GoalStats?,
                    @SerialName("passes")
                    val passes: PassStats?,
                    @SerialName("tackles")
                    val tackles: TackleStats?,
                    @SerialName("duels")
                    val duels: DuelStats?,
                    @SerialName("dribbles")
                    val dribbles: DribbleStats?,
                    @SerialName("fouls")
                    val fouls: FoulStats?,
                    @SerialName("cards")
                    val cards: CardStats?,
                    @SerialName("penalty")
                    val penalty: PenaltyStats?
                ) {
                    @Keep
                    @Serializable
                    data class GameStats(
                        @SerialName("minutes")
                        val minutes: Int?,
                        @SerialName("number")
                        val number: Int?,
                        @SerialName("position")
                        val position: String?,
                        @SerialName("rating")
                        val rating: String?,
                        @SerialName("captain")
                        val captain: Boolean?,
                        @SerialName("substitute")
                        val substitute: Boolean?
                    )

                    @Keep
                    @Serializable
                    data class ShotStats(
                        @SerialName("total")
                        val total: Int?,
                        @SerialName("on")
                        val on: Int?
                    )

                    @Keep
                    @Serializable
                    data class GoalStats(
                        @SerialName("total")
                        val total: Int?,
                        @SerialName("conceded")
                        val conceded: Int?,
                        @SerialName("assists")
                        val assists: Int?,
                        @SerialName("saves")
                        val saves: Int?
                    )

                    @Keep
                    @Serializable
                    data class PassStats(
                        @SerialName("total")
                        val total: Int?,
                        @SerialName("key")
                        val key: Int?,
                        @SerialName("accuracy")
                        val accuracy: String?
                    )

                    @Keep
                    @Serializable
                    data class TackleStats(
                        @SerialName("total")
                        val total: Int?,
                        @SerialName("blocks")
                        val blocks: Int?,
                        @SerialName("interceptions")
                        val interceptions: Int?
                    )

                    @Keep
                    @Serializable
                    data class DuelStats(
                        @SerialName("total")
                        val total: Int?,
                        @SerialName("won")
                        val won: Int?
                    )

                    @Keep
                    @Serializable
                    data class DribbleStats(
                        @SerialName("attempts")
                        val attempts: Int?,
                        @SerialName("success")
                        val success: Int?,
                        @SerialName("past")
                        val past: Int?
                    )

                    @Keep
                    @Serializable
                    data class FoulStats(
                        @SerialName("drawn")
                        val drawn: Int?,
                        @SerialName("committed")
                        val committed: Int?=null
                    )

                    @Keep
                    @Serializable
                    data class CardStats(
                        @SerialName("yellow")
                        val yellow: Int?,
                        @SerialName("red")
                        val red: Int?
                    )

                    @Keep
                    @Serializable
                    data class PenaltyStats(
                        @SerialName("won")
                        val won: Int? = null,
                        @SerialName("committed")
                        val committed: Int? = null,
                        @SerialName("scored")
                        val scored: Int? = null,
                        @SerialName("missed")
                        val missed: Int? = null,
                        @SerialName("saved")
                        val saved: Int? = null
                    )
                }
            }
        }
    }
}