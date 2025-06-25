package com.jerry.ronaldo.siufascore.domain.model

data class LeagueStandings(
    val league: StandingsLeagueInfo,
    val standingGroups: List<StandingGroup>
)

data class StandingsLeagueInfo(
    val id: Int,
    val name: String,
    val country: String,
    val logo: String,
    val flag: String,
    val season: Int
)

data class StandingGroup(
    val teams: List<TeamStanding>
)

data class TeamStanding(
    val rank: Int,
    val team: StandingTeamInfo,
    val points: Int,
    val goalsDifference: Int,
    val group: String?,
    val form: String?,
    val status: String?,
    val description: String?,
    val allGames: TeamStats,
    val homeGames: TeamStats,
    val awayGames: TeamStats,
    val lastUpdate: String?
){
    fun isTopFour(): Boolean = rank <= 4
    fun isEuropeanQualification(): Boolean = rank <= 7
    fun isRelegationZone(): Boolean = rank >= 18
}

data class StandingTeamInfo(
    val id: Int,
    val name: String,
    val logo: String
)

data class TeamStats(
    val played: Int,
    val wins: Int,
    val draws: Int,
    val losses: Int,
    val goalsFor: Int,
    val goalsAgainst: Int
) {
    // Computed properties
    val goalsDifference: Int get() = goalsFor - goalsAgainst
    val winPercentage: Float get() = if (played > 0) (wins.toFloat() / played) * 100 else 0f
    val points: Int get() = (wins * 3) + draws
}