package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.MatchResponse
import com.jerry.ronaldo.siufascore.data.model.MatchResponse.Response
import com.jerry.ronaldo.siufascore.data.model.MatchResponse.Response.Fixture.Status
import com.jerry.ronaldo.siufascore.data.model.MatchResponse.Response.Goals
import com.jerry.ronaldo.siufascore.data.model.MatchResponse.Response.Teams
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.MatchGoals
import com.jerry.ronaldo.siufascore.domain.model.MatchPeriods
import com.jerry.ronaldo.siufascore.domain.model.MatchScore
import com.jerry.ronaldo.siufascore.domain.model.MatchStatus
import com.jerry.ronaldo.siufascore.domain.model.ScoreDetail
import com.jerry.ronaldo.siufascore.domain.model.Team
import com.jerry.ronaldo.siufascore.domain.model.Venue


fun MatchResponse.toDomainMatchList(): List<Match> {
    return this.response.map { responseItem ->
        Match(
            id = responseItem.fixture.id,
            date = responseItem.fixture.date,
            timestamp = responseItem.fixture.timestamp,
            timezone = responseItem.fixture.timezone,
            status = responseItem.fixture.status.mapToMatchStatus(),
            venue = responseItem.fixture.venue.mapToVenue(),
            referee = responseItem.fixture.referee?:"Chưa có trọng tài",
            homeTeam = responseItem.teams.home.mapToTeam(),
            awayTeam = responseItem.teams.away.mapToTeam(),
            goals = responseItem.goals.mapToMatchGoals(),
            score = responseItem.score.mapToMatchScore(),
            league = responseItem.league.mapToLeague(),
            periods = responseItem.fixture.periods.mapToMatchPeriods()
        )
    }
}

fun Status.mapToMatchStatus(): MatchStatus {
    return MatchStatus(
        short = this.short,
        long = this.long,
        elapsed = this.elapsed?:0,
        extra = this.extra
    )
}

fun Response.Fixture.Venue?.mapToVenue(): Venue {
    return if(this != null){
        Venue(
            id = this.id?:0,
            name = this.name?:"Chưa rõ sân",
            city = this.city?:"Chưa rõ thành phố"
        )
    }else{
       Venue(
            id = 0,
            name = "Chưa rõ sân",
            city = "Chưa rõ thành phố"
        )
    }
}

fun Teams.Team.mapToTeam(): Team {
    return Team(
        id = this.id,
        name = this.name,
        logo = this.logo?:"",
        isWinner = this.winner
    )
}


fun Goals.mapToMatchGoals(): MatchGoals {
    return MatchGoals(
        home = this.home?:0,
        away = this.away?:0
    )
}

fun Response.Score.mapToMatchScore(): MatchScore {
    return MatchScore(
        halftime = ScoreDetail(
            home = this.halftime.home?:0,
            away = this.halftime.away?:0
        ),
        fulltime = ScoreDetail(
            home = this.fulltime.home?:0,
            away = this.fulltime.away?:0
        ),
        extratime = this.extratime.home?.toIntOrNull()?.let {
            ScoreDetail(
                home = it,
                away = this.extratime.away?.toIntOrNull()
            )
        },
        penalty = this.penalty.home?.toIntOrNull()?.let {
            ScoreDetail(
                home = it,
                away = this.penalty.away?.toIntOrNull()
            )
        }
    )
}

private fun Response.League.mapToLeague(): com.jerry.ronaldo.siufascore.domain.model.League {
    return com.jerry.ronaldo.siufascore.domain.model.League(
        id = this.id,
        name = this.name,
        country = this.country,
        logo = this.logo?:"",
        flag = this.flag?:"",
        season = this.season,
        round = this.round,
        hasStandings = this.standings
    )
}

fun Response.Fixture.Periods?.mapToMatchPeriods(): MatchPeriods? {
    return this?.let {
        MatchPeriods(
            first = it.first,
            second = it.second
        )
    }
}


