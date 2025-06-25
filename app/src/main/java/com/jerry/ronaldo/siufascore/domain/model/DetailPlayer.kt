package com.jerry.ronaldo.siufascore.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerStatistics(
    val player: PlayerSearch,
    val statistics: List<PlayerSeasonStats>
) {
    val currentSeasonStats: PlayerSeasonStats?
        get() = statistics.firstOrNull()

    val totalGoals: Int
        get() = statistics.sumOf { it.goals?.total ?: 0 }

    val totalAssists: Int
        get() = statistics.sumOf { it.goals?.assists ?: 0 }

    val totalAppearances: Int
        get() = statistics.sumOf { it.games?.appearences ?: 0 }
    val totalRating: Double
        get() = statistics.sumOf { it.games?.rating?.toDoubleOrNull() ?: 0.0 }
    val totalSubs = statistics.sumOf { it.substitutes?.subIn ?: 0 }



}


@Serializable
data class PlayerSeasonStats(
    val team: TeamInfo,
    val league: LeagueInfo,
    val games: GameStats?,
    val substitutes: SubstituteStats?,
    val shots: ShotStats?,
    val goals: GoalStats?,
    val passes: PassStats?,
    val tackles: TackleStats?,
    val duels: DuelStats?,
    val dribbles: DribbleStats?,
    val fouls: FoulStats?,
    val cards: CardStats?,
    val penalty: PenaltyStats?
) {
    // Key performance indicators
    val goalsPerGame: Double
        get() = goals?.total?.let { goals ->
            games?.appearences?.let { apps -> if (apps > 0) goals.toDouble() / apps else 0.0 }
        } ?: 0.0

    val assistsPerGame: Double
        get() = goals?.assists?.let { assists ->
            games?.appearences?.let { apps -> if (apps > 0) assists.toDouble() / apps else 0.0 }
        } ?: 0.0

    val passAccuracy: Double
        get() = passes?.let { pass ->
            val total = pass.total ?: 0
            val accuracy = pass.accuracy ?: 0
            if (total > 0) accuracy.toDouble() else 0.0
        } ?: 0.0

    val position: String? get() = games?.position
    val rating: Double? get() = games?.rating?.toDoubleOrNull()

}

@Serializable
data class TeamInfo (
    val id: Int = 0,
    val name: String= "",
    val logo: String? = null
)

@Serializable
data class LeagueInfo(
    val id: Int = 0,
    val name: String = "",
    val country: String? = null,
    val logo: String? = null,
    val flag: String? = null,
    val season: Int = 0
)

@Serializable
data class GameStats(
    val appearences: Int?,
    val lineups: Int?,
    val minutes: Int?,
    val number: Int?,
    val position: String?,
    val rating: String?,
    val captain: Boolean?
)

@Serializable
data class SubstituteStats(
    @SerialName("in") val subIn: Int?,
    @SerialName("out") val subOut: Int?,
    val bench: Int?
)

@Serializable
data class ShotStats(
    val total: Int?,
    val on: Int?
) {
    val accuracy: Double
        get() = if (total != null && total > 0 && on != null) {
            (on.toDouble() / total) * 100
        } else 0.0
}

@Serializable
data class GoalStats(
    val total: Int?,
    val conceded: Int?,
    val assists: Int?,
    val saves: Int?
)

@Serializable
data class PassStats(
    val total: Int?,
    val key: Int?,
    val accuracy: Int?
)

@Serializable
data class TackleStats(
    val total: Int?,
    val blocks: Int?,
    val interceptions: Int?
)

@Serializable
data class DuelStats(
    val total: Int?,
    val won: Int?
) {
    val winPercentage: Double
        get() = if (total != null && total > 0 && won != null) {
            (won.toDouble() / total) * 100
        } else 0.0
}

@Serializable
data class DribbleStats(
    val attempts: Int?,
    val success: Int?,
    val past: Int?
) {
    val successRate: Double
        get() = if (attempts != null && attempts > 0 && success != null) {
            (success.toDouble() / attempts) * 100
        } else 0.0
}

@Serializable
data class FoulStats(
    val drawn: Int?,
    val committed: Int?
)

@Serializable
data class CardStats(
    val yellow: Int?,
    val yellowred: Int?,
    val red: Int?
) {
    val totalCards: Int get() = (yellow ?: 0) + (yellowred ?: 0) + (red ?: 0)
}

@Serializable
data class PenaltyStats(
    val won: Int?,
    val commited: Int?,
    val scored: Int?,
    val missed: Int?,
    val saved: Int?
)

data class PlayerTeam(
    val teamId: Int,
    val teamName: String,
    val teamLogo: String,
    val seasons: List<Int>
)

data class PlayerTrophy(
    val country: String,
    val league: String,
    val place: String,
    val season: String
)

data class PlayerOverview(
    val playerId: Int,
    val teams: List<PlayerTeam>,
    val trophies: List<PlayerTrophy>
)