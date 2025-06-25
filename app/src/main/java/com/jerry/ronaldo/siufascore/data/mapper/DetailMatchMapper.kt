package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.DetailMatchResponse
import com.jerry.ronaldo.siufascore.domain.model.Coach
import com.jerry.ronaldo.siufascore.domain.model.DetailMatch
import com.jerry.ronaldo.siufascore.domain.model.EventPlayer
import com.jerry.ronaldo.siufascore.domain.model.EventTeam
import com.jerry.ronaldo.siufascore.domain.model.EventType
import com.jerry.ronaldo.siufascore.domain.model.League
import com.jerry.ronaldo.siufascore.domain.model.LineupPlayer
import com.jerry.ronaldo.siufascore.domain.model.LineupTeamInfo
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.MatchEvent
import com.jerry.ronaldo.siufascore.domain.model.MatchGoals
import com.jerry.ronaldo.siufascore.domain.model.MatchLineups
import com.jerry.ronaldo.siufascore.domain.model.MatchPeriods
import com.jerry.ronaldo.siufascore.domain.model.MatchScore
import com.jerry.ronaldo.siufascore.domain.model.MatchStatistics
import com.jerry.ronaldo.siufascore.domain.model.MatchStatus
import com.jerry.ronaldo.siufascore.domain.model.PlayerCardStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerDefenseStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerDribbleStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerDuelStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerFoulStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerInfo
import com.jerry.ronaldo.siufascore.domain.model.PlayerMatchStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerPassStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerShotStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerTeamInfo
import com.jerry.ronaldo.siufascore.domain.model.ScoreDetail
import com.jerry.ronaldo.siufascore.domain.model.StatTeamInfo
import com.jerry.ronaldo.siufascore.domain.model.Team
import com.jerry.ronaldo.siufascore.domain.model.TeamLineup
import com.jerry.ronaldo.siufascore.domain.model.TeamMatchStats
import com.jerry.ronaldo.siufascore.domain.model.TeamPlayerStats
import com.jerry.ronaldo.siufascore.domain.model.Venue

fun DetailMatchResponse.toDomain():DetailMatch? {
    return this.response.firstOrNull()?.toDomain()
}

fun DetailMatchResponse.FixtureDetailData.toDomain(): DetailMatch {
    return DetailMatch(
        match = this.toMatch(),
        events = this.events.map { it.toDomain() },
        lineups = this.lineups.toMatchLineups(),
        statistics = this.statistics.toMatchStatistics(),
        playerStatistics = this.players.map { it.toDomain(this.lineups) }
    )
}

//Match Mapper
fun DetailMatchResponse.FixtureDetailData.toMatch(): Match {
    val match = this.fixture
    return Match(
        id = match.id,
        date = match.date,
        timestamp = match.timestamp,
        timezone = match.timezone,
        status = match.status.toDomain(),
        venue = this.fixture.venue.toDomain(),
        referee = this.fixture.referee ?:"Chưa có trọng tài",
        homeTeam = this.teams.home.toDomain(),
        awayTeam = this.teams.away.toDomain(),
        goals = this.goals.toDomain(),
        score = this.score.toDomain(),
        league = this.league.toDomain(),
        periods = this.fixture.periods?.toDomain()
    )
}

fun DetailMatchResponse.FixtureDetailData.Fixture.Status.toDomain(): MatchStatus {
    return MatchStatus(
        short = this.short,
        long = this.long,
        elapsed = this.elapsed ?: 0,
        extra = this.extra
    )
}

fun DetailMatchResponse.FixtureDetailData.Fixture.Venue?.toDomain(): Venue {
    return if (this != null) {
        Venue(
            id = this.id ?: 0,
            name = this.name ?: "Chưa rõ sân đấu",
            city = this.city ?: "Chưa rõ địa điểm"
        )
    } else {
        Venue(0, "Chưa rõ sân đấu", "Chưa rõ địa điểm")
    }
}

private fun DetailMatchResponse.FixtureDetailData.Teams.Team.toDomain(): Team {
    return Team(
        id = this.id,
        name = this.name,
        logo = this.logo ?: "",
        isWinner = this.winner
    )
}

private fun DetailMatchResponse.FixtureDetailData.Goals.toDomain(): MatchGoals {
    return MatchGoals(
        home = this.home ?: 0,
        away = this.away ?: 0
    )
}

fun DetailMatchResponse.FixtureDetailData.Score.toDomain(): MatchScore {
    return MatchScore(
        halftime = ScoreDetail(this.halftime.home, this.halftime.away),
        fulltime = ScoreDetail(this.fulltime.home, this.fulltime.away),
        extratime = if (this.extratime.home != null || this.extratime.away != null) {
            ScoreDetail(this.extratime.home, this.extratime.away)
        } else null,
        penalty = if (this.penalty.home != null || this.penalty.away != null) {
            ScoreDetail(this.penalty.home, this.penalty.away)
        } else null
    )
}

private fun DetailMatchResponse.FixtureDetailData.League.toDomain(): League {
    return League(
        id = this.id,
        name = this.name,
        country = this.country,
        logo = this.logo ?: "",
        flag = this.flag ?: "",
        season = this.season,
        round = this.round,
        hasStandings = true
    )
}

private fun DetailMatchResponse.FixtureDetailData.Fixture.Periods.toDomain(): MatchPeriods {
    return MatchPeriods(
        first = this.first,
        second = this.second
    )
}

//eveent
fun DetailMatchResponse.FixtureDetailData.Event.toDomain(): MatchEvent {
    return MatchEvent(
        timeElapsed = this.time.elapsed,
        extraTime = this.time.extra,
        team = this.team.toDomain(),
        player = this.player.toDomain(),
        assist = this.assist?.toDomain(),
        type = EventType.fromApiType(this.type),
        detail = this.detail,
        comments = this.comments
    )
}

private fun DetailMatchResponse.FixtureDetailData.Event.EventTeam.toDomain(): EventTeam {
    return EventTeam(
        id = this.id,
        name = this.name,
        logo = this.logo ?: ""
    )
}

private fun DetailMatchResponse.FixtureDetailData.Event.EventPlayer.toDomain(): EventPlayer {
    return EventPlayer(
        id = this.id,
        name = this.name ?: "Chưa rõ cầu thủ"
    )
}

//line-up
fun List<DetailMatchResponse.FixtureDetailData.Lineup>.toMatchLineups(): MatchLineups? {
    val homeLineup = this.find { it.team.id != null }
    val awayLineup = this.find { it.team.id != homeLineup?.team?.id }

    return if (homeLineup != null && awayLineup != null) {
        MatchLineups(
            homeTeam = homeLineup.toDomain(),
            awayTeam = awayLineup.toDomain()
        )
    } else null
}

fun DetailMatchResponse.FixtureDetailData.Lineup.toDomain(): TeamLineup {
    return TeamLineup(
        team = this.team.toDomain(),
        coach = this.coach.toDomain(),
        formation = this.formation,
        lineUp = this.startXI.map { it.toDomain() },
        bench = this.substitutes.map { it.toDomain() }
    )
}

fun DetailMatchResponse.FixtureDetailData.Lineup.LineupTeam.toDomain(): LineupTeamInfo {
    return LineupTeamInfo(
        id = this.id,
        name = this.name,
        logo = this.logo ?: "",
        primaryColor = this.colors?.player?.primary,
        goalkeepterColor = this.colors?.goalkeeper?.primary
    )
}

private fun DetailMatchResponse.FixtureDetailData.Lineup.Coach.toDomain(): Coach {
    return Coach(
        id = this.id,
        name = this.name ?: "Unknown Coach",
        photo = this.photo
    )
}

private fun DetailMatchResponse.FixtureDetailData.Lineup.LineupPlayer.toDomain(): LineupPlayer {
    return LineupPlayer(
        id = this.player.id,
        name = this.player.name,
        shirtNumber = this.player.number,
        position = this.player.pos,
        gridPosition = this.player.grid,
        minutesPlayed = this.statistics?.minutesPlayed
    )
}

//statistic
fun List<DetailMatchResponse.FixtureDetailData.TeamStatistics>.toMatchStatistics(): MatchStatistics? {
    if (this.size < 2) return null

    val homeStats = this[0].toDomain()
    val awayStats = this[1].toDomain()

    return MatchStatistics(
        homeTeamStats = homeStats,
        awayTeamStats = awayStats
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamStatistics.toDomain(): TeamMatchStats {
    val statsMap = this.statistics.associate { it.type to it.value }

    return TeamMatchStats(
        team = this.team.toDomain(),
        possession = statsMap["Ball Possession"]?.removeSuffix("%")?.toIntOrNull(),
        shotsTotal = statsMap["Total Shots"]?.toIntOrNull(),
        shotsOnGoal = statsMap["Shots on Goal"]?.toIntOrNull(),
        shotsOffGoal = statsMap["Shots off Goal"]?.toIntOrNull(),
        shotsBlocked = statsMap["Blocked Shots"]?.toIntOrNull(),
        shotsInsideBox = statsMap["Shots insidebox"]?.toIntOrNull(),
        shotsOutsideBox = statsMap["Shots outsidebox"]?.toIntOrNull(),
        fouls = statsMap["Fouls"]?.toIntOrNull(),
        cornerKicks = statsMap["Corner Kicks"]?.toIntOrNull(),
        offsides = statsMap["Offsides"]?.toIntOrNull(),
        ballPossession = statsMap["Ball Possession"],
        yellowCards = statsMap["Yellow Cards"]?.toIntOrNull(),
        redCards = statsMap["Red Cards"]?.toIntOrNull(),
        goalkeeperSaves = statsMap["Goalkeeper Saves"]?.toIntOrNull(),
        totalPasses = statsMap["Total passes"]?.toIntOrNull(),
        passesAccurate = statsMap["Passes accurate"]?.toIntOrNull(),
        passAccuracy = statsMap["Passes %"]
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamStatistics.StatTeam.toDomain(): StatTeamInfo {
    return StatTeamInfo(
        id = this.id,
        name = this.name,
        logo = this.logo ?: ""
    )
}

//player-statistic
fun DetailMatchResponse.FixtureDetailData.TeamPlayers.toDomain(
    lineups: List<DetailMatchResponse.FixtureDetailData.Lineup>
): TeamPlayerStats {
    return TeamPlayerStats(
        team = this.team.toDomain(),
        players = this.players.map { it.toDomain(lineups) }
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerTeam.toDomain(): PlayerTeamInfo {
    return PlayerTeamInfo(
        id = this.id,
        name = this.name,
        logo = this.logo ?: ""
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerDetail.toDomain(
    lineups: List<DetailMatchResponse.FixtureDetailData.Lineup>
): PlayerMatchStats {
    val lineupPlayer = lineups.flatMap { it.startXI + it.substitutes }
        .find { it.player.id == this.player.id }
    val stats = this.statistics.firstOrNull()
    return PlayerMatchStats(
        player = this.player.toDomain(),
        minutesPlayed = stats?.games?.minutes,
        shirtNumber = stats?.games?.number,
        position = stats?.games?.position,
        rating = stats?.games?.rating?.toFloatOrNull(),
        isCaptain = stats?.games?.captain == true,
        isSubstitute = stats?.games?.substitute == true,
        goals = stats?.goals?.total,
        assists = stats?.goals?.assists,
        shots = stats?.shots?.toDomain(),
        passes = stats?.passes?.toDomain(),
        tackles = stats?.tackles?.toDomain(),
        duels = stats?.duels?.toDomain(),
        dribbles = stats?.dribbles?.toDomain(),
        fouls = stats?.fouls?.toDomain(),
        cards = stats?.cards?.toDomain(),
        gridPosition = lineupPlayer?.player?.grid
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerDetail.PlayerInfo.toDomain(): PlayerInfo {
    return PlayerInfo(
        id = this.id,
        name = this.name,
        photo = this.photo
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerDetail.PlayerMatchStatistics.ShotStats.toDomain(): PlayerShotStats {
    return PlayerShotStats(
        total = this.total,
        onTarget = this.on
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerDetail.PlayerMatchStatistics.PassStats.toDomain(): PlayerPassStats {
    return PlayerPassStats(
        total = this.total,
        keyPasses = this.key,
        accuracy = this.accuracy?.removeSuffix("%")?.toFloatOrNull()
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerDetail.PlayerMatchStatistics.TackleStats.toDomain(): PlayerDefenseStats {
    return PlayerDefenseStats(
        tackles = this.total,
        blocks = this.blocks,
        interceptions = this.interceptions
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerDetail.PlayerMatchStatistics.DuelStats.toDomain(): PlayerDuelStats {
    return PlayerDuelStats(
        total = this.total,
        won = this.won
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerDetail.PlayerMatchStatistics.DribbleStats.toDomain(): PlayerDribbleStats {
    return PlayerDribbleStats(
        attempts = this.attempts,
        successful = this.success
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerDetail.PlayerMatchStatistics.FoulStats.toDomain(): PlayerFoulStats {
    return PlayerFoulStats(
        drawn = this.drawn,
        committed = this.committed ?:0
    )
}

fun DetailMatchResponse.FixtureDetailData.TeamPlayers.PlayerDetail.PlayerMatchStatistics.CardStats.toDomain(): PlayerCardStats {
    return PlayerCardStats(
        yellow = this.yellow,
        red = this.red
    )
}