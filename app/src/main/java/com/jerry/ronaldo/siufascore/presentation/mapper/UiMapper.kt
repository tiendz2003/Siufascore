package com.jerry.ronaldo.siufascore.presentation.mapper

import com.jerry.ronaldo.siufascore.domain.model.LeagueStandings

data class TeamStandingItem(
    val position: Int,
    val teamName: String,
    val teamLogo: String,
    val points: Int,
    val playedGame: Int,
    val goalDifference: Int,
)

enum class PositionColor {
    CHAMPION,
    CHAMPIONS_LEAGUE,
    EUROPA_LEAGUE,
    SAFE,
    RELEGATION
}



fun List<LeagueStandings>.mapToTeamStandingItems(): List<TeamStandingItem>? {
    return this.firstOrNull()?.standingGroups?.firstOrNull()?.teams?.map { team ->
        TeamStandingItem(
            position = team.rank,
            teamName = team.team.name,
            teamLogo = team.team.logo,
            points = team.points,
            playedGame = team.allGames.played,
            goalDifference = team.goalsDifference,
        )
    }
}

fun getPositionColor(position: Int): PositionColor {
    return when (position) {
        1 -> PositionColor.CHAMPION
        in 2..4 -> PositionColor.CHAMPIONS_LEAGUE
        in 5..7 -> PositionColor.EUROPA_LEAGUE
        in 18..20 -> PositionColor.RELEGATION
        else -> PositionColor.SAFE
    }
}

