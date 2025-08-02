package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.AverageStatsDto
import com.jerry.ronaldo.siufascore.data.model.BiggestGoalsDetailDto
import com.jerry.ronaldo.siufascore.data.model.BiggestGoalsDto
import com.jerry.ronaldo.siufascore.data.model.BiggestStatsDto
import com.jerry.ronaldo.siufascore.data.model.BiggestWinLossDto
import com.jerry.ronaldo.siufascore.data.model.CardDetailDto
import com.jerry.ronaldo.siufascore.data.model.CardStatsDto
import com.jerry.ronaldo.siufascore.data.model.CardTimeStatsDto
import com.jerry.ronaldo.siufascore.data.model.CleanSheetStatsDto
import com.jerry.ronaldo.siufascore.data.model.FailedToScoreStatsDto
import com.jerry.ronaldo.siufascore.data.model.FixtureStatsDto
import com.jerry.ronaldo.siufascore.data.model.GoalForAgainstDto
import com.jerry.ronaldo.siufascore.data.model.GoalStatsDto
import com.jerry.ronaldo.siufascore.data.model.LeagueInfoDto
import com.jerry.ronaldo.siufascore.data.model.LineupStatsDto
import com.jerry.ronaldo.siufascore.data.model.MinuteStatsDto
import com.jerry.ronaldo.siufascore.data.model.PenaltyDetailDto
import com.jerry.ronaldo.siufascore.data.model.PenaltyStatsDto
import com.jerry.ronaldo.siufascore.data.model.PlayedStatsDto
import com.jerry.ronaldo.siufascore.data.model.StreakStatsDto
import com.jerry.ronaldo.siufascore.data.model.TeamInfoDto
import com.jerry.ronaldo.siufascore.data.model.TeamStatisticsDto
import com.jerry.ronaldo.siufascore.data.model.TotalStatsDto
import com.jerry.ronaldo.siufascore.data.model.WinLossStatsDto
import com.jerry.ronaldo.siufascore.domain.model.AverageStats
import com.jerry.ronaldo.siufascore.domain.model.BiggestGoals
import com.jerry.ronaldo.siufascore.domain.model.BiggestGoalsDetail
import com.jerry.ronaldo.siufascore.domain.model.BiggestStats
import com.jerry.ronaldo.siufascore.domain.model.BiggestWinLoss
import com.jerry.ronaldo.siufascore.domain.model.CardDetail
import com.jerry.ronaldo.siufascore.domain.model.CardTimeStats
import com.jerry.ronaldo.siufascore.domain.model.CleanSheetStats
import com.jerry.ronaldo.siufascore.domain.model.FailedToScoreStats
import com.jerry.ronaldo.siufascore.domain.model.FixtureStats
import com.jerry.ronaldo.siufascore.domain.model.GoalForAgainst
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.LineupStats
import com.jerry.ronaldo.siufascore.domain.model.MinuteStats
import com.jerry.ronaldo.siufascore.domain.model.PenaltyDetail
import com.jerry.ronaldo.siufascore.domain.model.PlayedStats
import com.jerry.ronaldo.siufascore.domain.model.StreakStats
import com.jerry.ronaldo.siufascore.domain.model.TeamCardStats
import com.jerry.ronaldo.siufascore.domain.model.TeamGoalStats
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamPenaltyStats
import com.jerry.ronaldo.siufascore.domain.model.TeamStatistics
import com.jerry.ronaldo.siufascore.domain.model.TotalStats
import com.jerry.ronaldo.siufascore.domain.model.WinLossStats

fun TeamStatisticsDto.toDomainModel(): TeamStatistics {
    return TeamStatistics(
        team = team.toDomainModel(),
        league = league.toDomainModel(),
        fixtures = fixtures.toDomainModel(),
        goals = goals.toDomainModel(),
        biggest = biggest.toDomainModel(),
        cleanSheet = clean_sheet.toDomainModel(),
        failedToScore = failed_to_score.toDomainModel(),
        penalty = penalty.toDomainModel(),
        lineups = lineups.map { it.toDomainModel() },
        cards = cards.toDomainModel()
    )
}

fun TeamInfoDto.toDomainModel() = TeamInfo(id, name, logo)

fun LeagueInfoDto.toDomainModel() = LeagueInfo(id, name, country, logo, flag, season)

fun FixtureStatsDto.toDomainModel() = FixtureStats(
    played = played.toDomainModel(),
    wins = wins.toDomainModel(),
    draws = draws.toDomainModel(),
    loses = loses.toDomainModel()
)

fun PlayedStatsDto.toDomainModel() = PlayedStats(home, away, total)

fun WinLossStatsDto.toDomainModel() = WinLossStats(home, away, total)

fun GoalStatsDto.toDomainModel() = TeamGoalStats(
    `for` = `for`.toDomainModel(),
    against = against.toDomainModel()
)

fun GoalForAgainstDto.toDomainModel() = GoalForAgainst(
    total = total.toDomainModel(),
    average = average.toDomainModel(),
    minute = minute.mapValues { it.value.toDomainModel() }
)

fun TotalStatsDto.toDomainModel() = TotalStats(home, away, total)

fun AverageStatsDto.toDomainModel() = AverageStats(home, away, total)

fun MinuteStatsDto.toDomainModel() = MinuteStats(total, percentage)

fun BiggestStatsDto.toDomainModel() = BiggestStats(
    streak = streak.toDomainModel(),
    wins = wins.toDomainModel(),
    loses = loses.toDomainModel(),
    goals = goals.toDomainModel()
)

fun StreakStatsDto.toDomainModel() = StreakStats(wins, draws, loses)

fun BiggestWinLossDto.toDomainModel() = BiggestWinLoss(home, away)

fun BiggestGoalsDto.toDomainModel() = BiggestGoals(
    `for` = `for`.toDomainModel(),
    against = against.toDomainModel()
)

fun BiggestGoalsDetailDto.toDomainModel() = BiggestGoalsDetail(home, away)

fun CleanSheetStatsDto.toDomainModel() = CleanSheetStats(home, away, total)

fun FailedToScoreStatsDto.toDomainModel() = FailedToScoreStats(home, away, total)

fun PenaltyStatsDto.toDomainModel() = TeamPenaltyStats(
    scored = scored.toDomainModel(),
    missed = missed.toDomainModel(),
    total = total
)

fun PenaltyDetailDto.toDomainModel() = PenaltyDetail(total, percentage)

fun LineupStatsDto.toDomainModel() = LineupStats(formation, played)

fun CardStatsDto.toDomainModel() = TeamCardStats(
    yellow = yellow.toDomainModel(),
    red = red.toDomainModel()
)

fun CardTimeStatsDto.toDomainModel() = CardTimeStats(
    `0-15` = `0-15`.toDomainModel(),
    `16-30` = `16-30`.toDomainModel(),
    `31-45` = `31-45`.toDomainModel(),
    `46-60` = `46-60`.toDomainModel(),
    `61-75` = `61-75`.toDomainModel(),
    `76-90` = `76-90`.toDomainModel(),
    `91-105` = `91-105`.toDomainModel(),
    `106-120` = `106-120`.toDomainModel()
)

fun CardDetailDto.toDomainModel() = CardDetail(total, percentage)