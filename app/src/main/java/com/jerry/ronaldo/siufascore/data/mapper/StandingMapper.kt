package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.StandingListResponse
import com.jerry.ronaldo.siufascore.data.model.StandingListResponse.AreaResponse
import com.jerry.ronaldo.siufascore.data.model.StandingListResponse.CompetitionResponse
import com.jerry.ronaldo.siufascore.data.model.StandingListResponse.SeasonResponse
import com.jerry.ronaldo.siufascore.data.model.StandingListResponse.StandingResponse
import com.jerry.ronaldo.siufascore.data.model.StandingListResponse.StandingResponse.TableResponse
import com.jerry.ronaldo.siufascore.data.model.StandingListResponse.StandingResponse.TableResponse.TeamResponse
import com.jerry.ronaldo.siufascore.domain.model.Area
import com.jerry.ronaldo.siufascore.domain.model.Competition
import com.jerry.ronaldo.siufascore.domain.model.Season
import com.jerry.ronaldo.siufascore.domain.model.Standing
import com.jerry.ronaldo.siufascore.domain.model.StandingData
import com.jerry.ronaldo.siufascore.domain.model.Team
import com.jerry.ronaldo.siufascore.domain.model.TeamStanding

fun StandingListResponse.toDomain():StandingData{
    return StandingData(
        area = area.toDomain(),
        competition = competition.toDomain(),
        season = season.toDomain(),
        standings = standings.map { it.toDomain() }
    )
}
fun AreaResponse.toDomain():Area{
    return Area(
        id = this.id,
        name = this.name,
        code = this.code,
        flag = this.flag
    )
}
fun CompetitionResponse.toDomain(): Competition {
    return Competition(
        id = id,
        name = name,
        code = code,
        emblem = emblem,
        type = type
    )
}
fun SeasonResponse.toDomain(): Season {
    return Season(
        id = id,
        startDate = startDate,
        endDate = endDate,
        currentMatchday = currentMatchday
    )
}
fun StandingResponse.toDomain(): Standing {
    return Standing(
        type = type,
        stage = stage,
        table = table.map { it.toDomain() }
    )
}
fun TableResponse.toDomain(): TeamStanding {
    return TeamStanding(
        position = position,
        team = team.toDomain(),
        playedGames = playedGames,
        won = won,
        draw = draw,
        lost = lost,
        goalsFor = goalsFor,
        goalsAgainst = goalsAgainst,
        goalDifference = goalDifference,
        points = points
    )
}
fun TeamResponse.toDomain(): Team {
    return Team(
        id = id,
        name = name,
        shortName = shortName,
        tla = tla,
        crest = crest
    )
}