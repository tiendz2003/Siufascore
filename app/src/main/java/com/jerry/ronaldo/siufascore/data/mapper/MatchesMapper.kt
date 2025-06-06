package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.MatchesResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.CompetitionResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse.AreaResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse.RefereeResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse.ScoreResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse.ScoreResponse.ExtraTimeResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse.ScoreResponse.PenaltiesResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse.ScoreResponse.RegularTimeResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse.ScoreResponse.ScoreDetailResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse.SeasonResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse.MatcheResponse.TeamResponse
import com.jerry.ronaldo.siufascore.domain.model.Area
import com.jerry.ronaldo.siufascore.domain.model.Competition
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.Referee
import com.jerry.ronaldo.siufascore.domain.model.Score
import com.jerry.ronaldo.siufascore.domain.model.ScoreDetail
import com.jerry.ronaldo.siufascore.domain.model.Season
import com.jerry.ronaldo.siufascore.domain.model.Team

fun CompetitionResponse.toDomain(): Competition {
    return Competition(
        id = this.id,
        name = this.name,
        code = this.code,
        type = this.type,
        emblem = this.emblem,
    )
}

fun AreaResponse.toDomain(): Area {
    return Area(
        id = this.id,
        name = this.name,
        code = this.code,
        flag = this.name
    )
}

fun SeasonResponse.toDomain(): Season {
    return Season(
        id = this.id,
        startDate = this.startDate,
        endDate = this.endDate,
        currentMatchday = this.currentMatchday
    )
}

fun TeamResponse.toDomain(): Team {
    return Team(
        id = this.id,
        name = this.name,
        shortName = this.shortName,
        tla = this.tla,
        crest = this.crest
    )
}

fun ScoreDetailResponse.toDomain(): ScoreDetail {
    return ScoreDetail(
        home = this.home?:0,
        away = this.away?:0
    )
}

fun ExtraTimeResponse?.toDomain(): ScoreDetail? {
    return this?.let {
        ScoreDetail(
            home = it.home,
            away = it.away
        )
    }
}

fun PenaltiesResponse?.toDomain(): ScoreDetail? {
    return this?.let {
        ScoreDetail(
            home = it.home,
            away = it.away
        )
    }
}
fun RegularTimeResponse?.toDomain(): ScoreDetail? {
    return this?.let {
        ScoreDetail(
            home = it.home,
            away = it.away
        )
    }
}

fun ScoreResponse.toDomain(): Score {
    return Score(
        winner = this.winner,
        duration = this.duration,
        fullTime = this.fullTime.toDomain(),
        halfTime = this.halfTime.toDomain(),
        extraTime = this.extraTime.toDomain(),
        penalties = this.penalties.toDomain(),
        regularTime = this.regularTime.toDomain()
    )
}

fun RefereeResponse.toDomain(): Referee {
    return Referee(
        id = this.id,
        name = this.name,
        nationality = this.nationality,
        type = this.type
    )
}

fun MatcheResponse.toDomain(): Match {
    return Match(
        id = this.id,
        utcDate = this.utcDate,
        status = this.status,
        matchday = this.matchday,
        stage = this.stage,
        homeTeam = this.homeTeam.toDomain(),
        awayTeam = this.awayTeam.toDomain(),
        score = this.score.toDomain(),
        area = this.area.toDomain(),
        season = this.season.toDomain()
    )
}

fun MatchesResponse.toDomainMatchList(): List<Match> {
    return matches.map {
        it.toDomain()
    }
}

