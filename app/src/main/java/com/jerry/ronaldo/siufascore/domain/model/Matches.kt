package com.jerry.ronaldo.siufascore.domain.model

//v2
data class Match(
    val id: Int,
    val date: String,
    val timestamp: Int,
    val timezone: String,
    val status: MatchStatus,
    val venue: Venue,
    val referee: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val goals: MatchGoals,
    val score: MatchScore,
    val league: League,
    val periods: MatchPeriods?
)

data class MatchStatus(
    val short: String,
    val long: String,
    val elapsed: Int,
    val extra: String?
)

data class Venue(
    val id: Int,
    val name: String,
    val city: String
)

data class Team(
    val id: Int,
    val name: String,
    val logo: String,
    val isWinner: Boolean?
)

data class MatchGoals(
    val home: Int,
    val away: Int
)

data class MatchScore(
    val halftime: ScoreDetail,
    val fulltime: ScoreDetail,
    val extratime: ScoreDetail?,
    val penalty: ScoreDetail?
)

data class ScoreDetail(
    val home: Int?,
    val away: Int?
)

data class League(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String,
    val season: Int,
    val round: String,
    val hasStandings: Boolean
)

data class MatchPeriods(
    val first: Int?,
    val second: Int?
)
