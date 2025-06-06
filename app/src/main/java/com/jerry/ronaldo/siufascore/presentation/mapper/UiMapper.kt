package com.jerry.ronaldo.siufascore.presentation.mapper

import com.jerry.ronaldo.siufascore.domain.model.LeagueStanding
import com.jerry.ronaldo.siufascore.domain.model.StandingData

data class TeamStandingItem(
    val position:Int,
    val teamName:String,
    val teamLogo:String,
    val points:Int,
    val playedGame:Int,
    val goalDifference:Int,
    val positionColor: PositionColor,
    val isHighlighted: Boolean
)
enum class PositionColor{
    CHAMPION,
    CHAMPIONS_LEAGUE,
    EUROPA_LEAGUE,
    SAFE,
    RELEGATION
}
fun StandingData.mapToLeagueTable(): LeagueStanding {
    val mainStanding = this.standings.first()
    return LeagueStanding(
        competition = this.competition,
        season = this.season,
        teams = mainStanding.table
    )
}
fun LeagueStanding.mapToTeamStandingItems(): List<TeamStandingItem> {
    return this.teams.map { team ->
        TeamStandingItem(
            position = team.position,
            teamName = team.team.tla,
            teamLogo = team.team.crest,
            points = team.points,
            playedGame = team.playedGames,
            goalDifference = team.goalDifference,
            isHighlighted = team.isTopFour() || team.isEuropeanQualification(),
            positionColor = getPositionColor(team.position),
        )
    }
}

fun getPositionColor(position: Int): PositionColor {
    return when(position){
        1->PositionColor.CHAMPION
        in 2..4 -> PositionColor.CHAMPIONS_LEAGUE
        in 5..7 -> PositionColor.EUROPA_LEAGUE
        in 18..20 -> PositionColor.RELEGATION
        else -> PositionColor.SAFE
    }
}

