package com.jerry.ronaldo.siufascore.domain.model

data class Competition(
    val id: Int,
    val name: String,
    val code: String,
    val type: String,
    val emblem: String,
)

data class Area(
    val id: Int,
    val name: String,
    val code: String,
    val flag: String
)

data class Season(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val currentMatchday: Int
)

data class Team(
    val id: Int,
    val name: String,
    val shortName: String,
    val tla: String,
    val crest: String
)

data class Match(
    val id: Int,
    val utcDate: String,
    val status: String,
    val matchday: Int,
    val stage: String,
    val homeTeam: Team,
    val awayTeam: Team,
    val score: Score,
    val area: Area,
    val season: Season,
    val competition: Competition,
)

data class Score(
    val winner: String?,
    val duration: String,
    val fullTime: ScoreDetail,
    val halfTime: ScoreDetail,
    val extraTime: ScoreDetail? = null,
    val penalties: ScoreDetail? = null,
    val regularTime: ScoreDetail? = null
)

data class ScoreDetail(
    val home: Int? = null,
    val away: Int? = null
)

data class Referee(
    val id: Int,
    val name: String,
    val nationality: String,
    val type: String
)
//"-----------------------------Bảng xếp hạng----------------------"
data class StandingData(
    val area: Area,
    val competition: Competition,
    val season: Season,
    val standings: List<Standing>
)
data class Standing(
    val type: String,
    val stage: String,
    val table: List<TeamStanding>
)

data class TeamStanding(
    val position: Int,
    val team: Team,
    val playedGames: Int,
    val won: Int,
    val draw: Int,
    val lost: Int,
    val goalsFor: Int,
    val goalsAgainst: Int,
    val goalDifference: Int,
    val points: Int
) {
    val winRate: Float get() = if (playedGames > 0) won.toFloat() / playedGames else 0f
    val formText: String get() = "W:$won D:$draw L:$lost"
    val goalsText: String get() = "$goalsFor:$goalsAgainst"

    fun isTopFour(): Boolean = position <= 4
    fun isEuropeanQualification(): Boolean = position <= 7
    fun isRelegationZone(): Boolean = position >= 18
}

data class LeagueStanding(
    val competition: Competition,
    val season: Season,
    val teams: List<TeamStanding>
) {
    val totalTeams: Int get() = teams.size
    val topFourTeams: List<TeamStanding> get() = teams.filter { it.isTopFour() }
    val europeanQualificationTeams: List<TeamStanding> get() = teams.filter { it.isEuropeanQualification() }
    val relegationTeams: List<TeamStanding> get() = teams.filter { it.isRelegationZone() }
}
