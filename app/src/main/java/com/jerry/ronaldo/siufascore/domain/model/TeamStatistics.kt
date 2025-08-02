package com.jerry.ronaldo.siufascore.domain.model

data class TeamStatistics(
    val team: TeamInfo,
    val league: LeagueInfo,
    val fixtures: FixtureStats,
    val goals: TeamGoalStats,
    val biggest: BiggestStats,
    val cleanSheet: CleanSheetStats,
    val failedToScore: FailedToScoreStats,
    val penalty: TeamPenaltyStats,
    val lineups: List<LineupStats>,
    val cards: TeamCardStats
)



data class FixtureStats(
    val played: PlayedStats,
    val wins: WinLossStats,
    val draws: WinLossStats,
    val loses: WinLossStats
)

data class PlayedStats(
    val home: Int,
    val away: Int,
    val total: Int
)

data class WinLossStats(
    val home: Int,
    val away: Int,
    val total: Int
)

data class TeamGoalStats(
    val `for`: GoalForAgainst,
    val against: GoalForAgainst
)

data class GoalForAgainst(
    val total: TotalStats,
    val average: AverageStats,
    val minute: Map<String, MinuteStats>
)

data class TotalStats(
    val home: Int,
    val away: Int,
    val total: Int
)

data class AverageStats(
    val home: String,
    val away: String,
    val total: String
)

data class MinuteStats(
    val total: Int?,
    val percentage: String?
)

data class BiggestStats(
    val streak: StreakStats,
    val wins: BiggestWinLoss,
    val loses: BiggestWinLoss,
    val goals: BiggestGoals
)

data class StreakStats(
    val wins: Int,
    val draws: Int,
    val loses: Int
)

data class BiggestWinLoss(
    val home: String?,
    val away: String?
)

data class BiggestGoals(
    val `for`: BiggestGoalsDetail,
    val against: BiggestGoalsDetail
)

data class BiggestGoalsDetail(
    val home: Int,
    val away: Int
)

data class CleanSheetStats(
    val home: Int,
    val away: Int,
    val total: Int
)

data class FailedToScoreStats(
    val home: Int,
    val away: Int,
    val total: Int
)

data class TeamPenaltyStats(
    val scored: PenaltyDetail,
    val missed: PenaltyDetail,
    val total: Int
)

data class PenaltyDetail(
    val total: Int,
    val percentage: String
)

data class LineupStats(
    val formation: String,
    val played: Int
)

data class TeamCardStats(
    val yellow: CardTimeStats,
    val red: CardTimeStats
)

data class CardTimeStats(
    val `0-15`: CardDetail,
    val `16-30`: CardDetail,
    val `31-45`: CardDetail,
    val `46-60`: CardDetail,
    val `61-75`: CardDetail,
    val `76-90`: CardDetail,
    val `91-105`: CardDetail,
    val `106-120`: CardDetail
)

data class CardDetail(
    val total: Int?,
    val percentage: String?
)