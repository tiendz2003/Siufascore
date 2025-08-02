package com.jerry.ronaldo.siufascore.data.model

data class TeamStatisticsResponse(
    val get: String,
    val parameters: Map<String, String>,
    val errors: List<String>,
    val results: Int,
    val paging: Paging,
    val response: List<TeamStatisticsDto>
)

data class Paging(
    val current: Int,
    val total: Int
)

data class TeamStatisticsDto(
    val team: TeamInfoDto,
    val league: LeagueInfoDto,
    val fixtures: FixtureStatsDto,
    val goals: GoalStatsDto,
    val biggest: BiggestStatsDto,
    val clean_sheet: CleanSheetStatsDto,
    val failed_to_score: FailedToScoreStatsDto,
    val penalty: PenaltyStatsDto,
    val lineups: List<LineupStatsDto>,
    val cards: CardStatsDto
)

data class TeamInfoDto(
    val id: Int,
    val name: String,
    val logo: String
)

data class LeagueInfoDto(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String,
    val season: Int
)

data class FixtureStatsDto(
    val played: PlayedStatsDto,
    val wins: WinLossStatsDto,
    val draws: WinLossStatsDto,
    val loses: WinLossStatsDto
)

data class PlayedStatsDto(
    val home: Int,
    val away: Int,
    val total: Int
)

data class WinLossStatsDto(
    val home: Int,
    val away: Int,
    val total: Int
)

data class GoalStatsDto(
    val `for`: GoalForAgainstDto,
    val against: GoalForAgainstDto
)

data class GoalForAgainstDto(
    val total: TotalStatsDto,
    val average: AverageStatsDto,
    val minute: Map<String, MinuteStatsDto>
)

data class TotalStatsDto(
    val home: Int,
    val away: Int,
    val total: Int
)

data class AverageStatsDto(
    val home: String,
    val away: String,
    val total: String
)

data class MinuteStatsDto(
    val total: Int?,
    val percentage: String?
)

data class BiggestStatsDto(
    val streak: StreakStatsDto,
    val wins: BiggestWinLossDto,
    val loses: BiggestWinLossDto,
    val goals: BiggestGoalsDto
)

data class StreakStatsDto(
    val wins: Int,
    val draws: Int,
    val loses: Int
)

data class BiggestWinLossDto(
    val home: String?,
    val away: String?
)

data class BiggestGoalsDto(
    val `for`: BiggestGoalsDetailDto,
    val against: BiggestGoalsDetailDto
)

data class BiggestGoalsDetailDto(
    val home: Int,
    val away: Int
)

data class CleanSheetStatsDto(
    val home: Int,
    val away: Int,
    val total: Int
)

data class FailedToScoreStatsDto(
    val home: Int,
    val away: Int,
    val total: Int
)

data class PenaltyStatsDto(
    val scored: PenaltyDetailDto,
    val missed: PenaltyDetailDto,
    val total: Int
)

data class PenaltyDetailDto(
    val total: Int,
    val percentage: String
)

data class LineupStatsDto(
    val formation: String,
    val played: Int
)

data class CardStatsDto(
    val yellow: CardTimeStatsDto,
    val red: CardTimeStatsDto
)

data class CardTimeStatsDto(
    val `0-15`: CardDetailDto,
    val `16-30`: CardDetailDto,
    val `31-45`: CardDetailDto,
    val `46-60`: CardDetailDto,
    val `61-75`: CardDetailDto,
    val `76-90`: CardDetailDto,
    val `91-105`: CardDetailDto,
    val `106-120`: CardDetailDto
)

data class CardDetailDto(
    val total: Int?,
    val percentage: String?
)