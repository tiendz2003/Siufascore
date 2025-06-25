package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.StandingsResponse
import com.jerry.ronaldo.siufascore.data.model.StandingsResponse.StandingsDataResponse.StandingTeam
import com.jerry.ronaldo.siufascore.data.model.StandingsResponse.StandingsDataResponse.StandingTeam.StandingStats
import com.jerry.ronaldo.siufascore.data.model.StandingsResponse.StandingsDataResponse.StandingsLeague
import com.jerry.ronaldo.siufascore.domain.model.LeagueStandings
import com.jerry.ronaldo.siufascore.domain.model.StandingGroup
import com.jerry.ronaldo.siufascore.domain.model.StandingTeamInfo
import com.jerry.ronaldo.siufascore.domain.model.StandingsLeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamStanding
import com.jerry.ronaldo.siufascore.domain.model.TeamStats

fun StandingsResponse.toDomain(): List<LeagueStandings> {
    return this.response.map { standingsData ->
        standingsData.toDomain()
    }
}

fun StandingsResponse.StandingsDataResponse.toDomain(): LeagueStandings {
    return LeagueStandings(
        league = this.league.mapToStandingsLeagueInfo(),
        standingGroups = this.league.standings.mapToStandingGroups()
    )
}

private fun StandingsLeague.mapToStandingsLeagueInfo(): StandingsLeagueInfo {
    return StandingsLeagueInfo(
        id = this.id,
        name = this.name,
        country = this.country,
        logo = this.logo ?: "",
        flag = this.flag ?: "",
        season = this.season
    )
}

fun List<List<StandingTeam>>.mapToStandingGroups(): List<StandingGroup> {
    return this.map { group ->
        StandingGroup(
            teams = group.map { teamStanding ->
                teamStanding.toDomain()
            }
        )
    }
}

fun StandingTeam.toDomain(): TeamStanding {
    return TeamStanding(
        rank = this.rank,
        team = this.team.toDomain(),
        points = this.points,
        goalsDifference = this.goalsDiff,
        group = this.group,
        form = this.form,
        status = this.status,
        description = this.description,
        allGames = this.all.toDomain(),
        homeGames = this.home.toDomain(),
        awayGames = this.away.toDomain(),
        lastUpdate = this.update
    )
}

fun StandingTeam.StandingTeamInfo.toDomain(): StandingTeamInfo {
    return StandingTeamInfo(
        id = this.id,
        name = this.name,
        logo = this.logo ?: ""
    )
}

fun StandingStats.toDomain(): TeamStats {
    return TeamStats(
        played = this.played,
        wins = this.win,
        draws = this.draw,
        losses = this.lose,
        goalsFor = this.goals.goalsFor,
        goalsAgainst = this.goals.goalsAgainst
    )
}
