package com.jerry.ronaldo.siufascore.data.model

import androidx.annotation.Keep
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class TeamDetailResponse(
    val get: String,
    val parameters: Parameters,
    val errors: List<String> = emptyList(),
    val results: Int,
    val response: ResponseTeamStatistics
)

@Serializable data class Parameters(val league: String, val season: String, val team: String)

@Serializable
data class ResponseTeamStatistics(
    val league: LeagueInfo,
    val team: TeamInfo,
    val form: String,
    val fixtures: Fixtures,
    val goals: Goals,
    val biggest: Biggest,
    @SerialName("clean_sheet") val cleanSheet: HomeAwayTotalInt,
    @SerialName("failed_to_score") val failedToScore: HomeAwayTotalInt,
    val penalty: Penalty,
    val lineups: List<Lineup>,
)



@Serializable
data class Fixtures(
    val played: HomeAwayTotalInt,
    val wins: HomeAwayTotalInt,
    val draws: HomeAwayTotalInt,
    val loses: HomeAwayTotalInt
)

@Serializable
data class Goals(
    @SerialName("for") val forGoals: ForAgainst,
    val against: ForAgainst
)

@Serializable
data class ForAgainst(
    val total: HomeAwayTotalInt,

)


@Serializable
data class Biggest(
    val streak: Streak,
    val wins: HomeAwayString,
    val loses: HomeAwayString,
    val goals: GoalsBiggest
)

@Serializable data class Streak(val wins: Int, val draws: Int, val loses: Int)
@Serializable data class GoalsBiggest(@SerialName("for") val forGoals: HomeAwayInt, val against: HomeAwayInt)

@Serializable
data class Penalty(
    val scored: PenaltyDetail,
    val missed: PenaltyDetail,
    val total: Int
)

@Serializable data class PenaltyDetail(val total: Int, val percentage: String)
@Serializable data class Lineup(val formation: String, val played: Int)

@Serializable data class HomeAwayTotalInt(val home: Int, val away: Int, val total: Int)
@Serializable data class HomeAwayString(val home: String, val away: String)
@Serializable data class HomeAwayInt(val home: Int, val away: Int)