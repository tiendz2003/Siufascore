package com.jerry.ronaldo.siufascore.domain.model

data class DetailMatch(
    val match: Match,
    val events: List<MatchEvent>,
    val lineups: MatchLineups?,
    val statistics: MatchStatistics?,
    val playerStatistics: List<TeamPlayerStats>
) {
    val hasEvents: Boolean get() = events.isNotEmpty()
    val hasLineups: Boolean get() = lineups != null
    val hasStatistics: Boolean get() = statistics != null
    val hasPlayerStats: Boolean get() = playerStatistics.isNotEmpty()

    // Event categories
    val goalEvents: List<MatchEvent> get() = events.filter { it.type == EventType.GOAL }
    val cardEvents: List<MatchEvent> get() = events.filter { it.type == EventType.CARD }
    val substitutionEvents: List<MatchEvent> get() = events.filter { it.type == EventType.SUBSTITUTION }

    // Timeline events (goals, cards, subs) sorted by time
    val timelineEvents: List<MatchEvent>
        get() = events
            .filter { it.type in listOf(EventType.GOAL, EventType.CARD, EventType.SUBSTITUTION) }
            .sortedBy { it.timeElapsed }

    // Top performers
    val topScorers: List<PlayerMatchStats>
        get() = playerStatistics.flatMap { it.players }
            .filter { (it.goals ?: 0) > 0 }
            .sortedByDescending { it.goals }

    val topRatedPlayers: List<PlayerMatchStats>
        get() = playerStatistics.flatMap { it.players }
            .filter { it.hasPlayedTime && it.rating != null }
            .sortedByDescending { it.rating }
            .take(5)
}

// ==================== MATCH EVENTS ====================

data class MatchEvent(
    val timeElapsed: Int,
    val extraTime: Int?,
    val team: EventTeam,
    val player: EventPlayer,
    val assist: EventPlayer?,
    val type: EventType,
    val detail: String,
    val comments: String?
) {
    val displayTime: String get() = if (extraTime != null) "$timeElapsed+$extraTime'" else "$timeElapsed'"
    val isGoal: Boolean get() = type == EventType.GOAL
    val isCard: Boolean get() = type == EventType.CARD
    val isSubstitution: Boolean get() = type == EventType.SUBSTITUTION
    val isYellowCard: Boolean get() = isCard && detail.contains("Yellow", ignoreCase = true)
    val isRedCard: Boolean get() = isCard && detail.contains("Red", ignoreCase = true)
}

data class EventTeam(
    val id: Int,
    val name: String,
    val logo: String
)

data class EventPlayer(
    val id: Int?,
    val name: String
) {
    val shortName: String
        get() = name.split(" ").let { parts ->
            when {
                parts.size == 1 -> parts[0]
                parts.size >= 2 -> "${parts.first().first()}. ${parts.last()}"
                else -> name
            }
        }
}

enum class EventType(val displayName: String, val iconRes: String) {
    GOAL("Bàn thắng", "⚽"),
    CARD("Thẻ phạt", "🟨"),
    SUBSTITUTION("Thay người", "🔄"),
    VAR("VAR", "📹"),
    UNKNOWN("Khác", "❓");

    companion object {
        fun fromApiType(type: String): EventType = when (type.lowercase()) {
            "goal" -> GOAL
            "card" -> CARD
            "subst" -> SUBSTITUTION
            "var" -> VAR
            else -> UNKNOWN
        }
    }
}

// ==================== LINEUPS ====================

data class MatchLineups(
    val homeTeam: TeamLineup,
    val awayTeam: TeamLineup
)

data class TeamLineup(
    val team: LineupTeamInfo,
    val coach: Coach,
    val formation: String?,
    val lineUp: List<LineupPlayer>,
    val bench: List<LineupPlayer>
) {
    val formationDisplay: String get() = formation ?: "Unknown"
    val totalPlayers: Int get() = lineUp.size + bench.size
}

data class LineupTeamInfo(
    val id: Int,
    val name: String,
    val logo: String,
    val primaryColor: String?,
    val goalkeepterColor: String?
)

data class Coach(
    val id: Int?,
    val name: String,
    val photo: String?
)

data class LineupPlayer(
    val id: Int,
    val name: String,
    val shirtNumber: Int?,
    val position: String?,
    val gridPosition: String?,
    val minutesPlayed: Int?
) {
    val shortPosition: String
        get() = when (position?.uppercase()) {
            "GOALKEEPER" -> "GK"
            "DEFENDER" -> "DEF"
            "MIDFIELDER" -> "MID"
            "ATTACKER" -> "ATT"
            else -> position?.take(3) ?: "???"
        }
    val displayName: String get() = name.split(" ").takeLast(1).firstOrNull() ?: name
}

// ==================== STATISTICS ====================

data class MatchStatistics(
    val homeTeamStats: TeamMatchStats,
    val awayTeamStats: TeamMatchStats
) {
    fun getComparisonStats(): List<StatComparison> {
        return listOf(
            StatComparison(
                label = "Ball Possession",
                homeValue = homeTeamStats.ballPossession ?: "0%",
                awayValue = awayTeamStats.ballPossession ?: "0%",
                homeProgress = homeTeamStats.possessionPercentage ?: 0f,
                awayProgress = awayTeamStats.possessionPercentage ?: 0f
            ),
            StatComparison(
                label = "Total Shots",
                homeValue = homeTeamStats.shotsTotal?.toString() ?: "0",
                awayValue = awayTeamStats.shotsTotal?.toString() ?: "0",
                homeProgress = compareValues(
                    homeTeamStats.shotsTotal,
                    awayTeamStats.shotsTotal,
                    true
                ),
                awayProgress = compareValues(
                    awayTeamStats.shotsTotal,
                    homeTeamStats.shotsTotal,
                    true
                )
            ),
            StatComparison(
                label = "Shots on Goal",
                homeValue = homeTeamStats.shotsOnGoal?.toString() ?: "0",
                awayValue = awayTeamStats.shotsOnGoal?.toString() ?: "0",
                homeProgress = compareValues(
                    homeTeamStats.shotsOnGoal,
                    awayTeamStats.shotsOnGoal,
                    true
                ),
                awayProgress = compareValues(
                    awayTeamStats.shotsOnGoal,
                    homeTeamStats.shotsOnGoal,
                    true
                )
            ),
            StatComparison(
                label = "Pass Accuracy",
                homeValue = homeTeamStats.passAccuracy ?: "0%",
                awayValue = awayTeamStats.passAccuracy ?: "0%",
                homeProgress = homeTeamStats.passAccuracyPercentage ?: 0f,
                awayProgress = awayTeamStats.passAccuracyPercentage ?: 0f
            ),
            StatComparison(
                label = "Fouls",
                homeValue = homeTeamStats.fouls?.toString() ?: "0",
                awayValue = awayTeamStats.fouls?.toString() ?: "0",
                homeProgress = compareValues(homeTeamStats.fouls, awayTeamStats.fouls, false),
                awayProgress = compareValues(awayTeamStats.fouls, homeTeamStats.fouls, false)
            ),
            StatComparison(
                label = "Corner Kicks",
                homeValue = homeTeamStats.cornerKicks?.toString() ?: "0",
                awayValue = awayTeamStats.cornerKicks?.toString() ?: "0",
                homeProgress = compareValues(
                    homeTeamStats.cornerKicks,
                    awayTeamStats.cornerKicks,
                    true
                ),
                awayProgress = compareValues(
                    awayTeamStats.cornerKicks,
                    homeTeamStats.cornerKicks,
                    true
                )
            )
        )
    }

    private fun compareValues(value1: Int?, value2: Int?, higherIsBetter: Boolean): Float {
        if (value1 == null || value2 == null) return 0f
        val total = value1 + value2
        if (total == 0) return 50f

        val percentage = (value1.toFloat() / total) * 100
        return if (higherIsBetter) percentage else 100f - percentage
    }

}

data class StatComparison(
    val label: String,
    val homeValue: String,
    val awayValue: String,
    val homeProgress: Float, // 0-100
    val awayProgress: Float  // 0-100
)

data class TeamMatchStats(
    val team: StatTeamInfo,
    val possession: Int?,
    val shotsTotal: Int?,
    val shotsOnGoal: Int?,
    val shotsOffGoal: Int?,
    val shotsBlocked: Int?,
    val shotsInsideBox: Int?,
    val shotsOutsideBox: Int?,
    val fouls: Int?,
    val cornerKicks: Int?,
    val offsides: Int?,
    val ballPossession: String?,
    val yellowCards: Int?,
    val redCards: Int?,
    val goalkeeperSaves: Int?,
    val totalPasses: Int?,
    val passesAccurate: Int?,
    val passAccuracy: String?
) {
    val passAccuracyPercentage: Float? get() = passAccuracy?.removeSuffix("%")?.toFloatOrNull()
    val possessionPercentage: Float? get() = ballPossession?.removeSuffix("%")?.toFloatOrNull()
    val shotAccuracy: Float? get() = if (shotsTotal != null && shotsTotal > 0 && shotsOnGoal != null) {
        (shotsOnGoal.toFloat() / shotsTotal) * 100
    } else null
}

data class StatTeamInfo(
    val id: Int,
    val name: String,
    val logo: String
)

// ==================== PLAYER STATISTICS ====================

data class TeamPlayerStats(
    val team: PlayerTeamInfo,
    val players: List<PlayerMatchStats>
){
    val topRatedPlayers: List<PlayerMatchStats> get() = players
        .filter { it.hasPlayedTime && it.rating != null }
        .sortedByDescending { it.rating }
        .take(3)

    val goalScorers: List<PlayerMatchStats> get() = players
        .filter { (it.goals ?: 0) > 0 }
        .sortedByDescending { it.goals }

    val assistProviders: List<PlayerMatchStats> get() = players
        .filter { (it.assists ?: 0) > 0 }
        .sortedByDescending { it.assists }

    val startingXI: List<PlayerMatchStats> get() = players
        .filter { !it.isSubstitute }
        .sortedBy { it.shirtNumber }

    val substitutes: List<PlayerMatchStats> get() = players
        .filter { it.isSubstitute }
        .sortedBy { it.shirtNumber }
}

data class PlayerTeamInfo(
    val id: Int,
    val name: String,
    val logo: String
)

data class PlayerMatchStats(
    val player: PlayerInfo,
    val minutesPlayed: Int?,
    val shirtNumber: Int?,
    val position: String?,
    val rating: Float?,
    val isCaptain: Boolean,
    val isSubstitute: Boolean,
    val goals: Int?,
    val assists: Int?,
    val shots: PlayerShotStats?,
    val passes: PlayerPassStats?,
    val tackles: PlayerDefenseStats?,
    val duels: PlayerDuelStats?,
    val dribbles: PlayerDribbleStats?,
    val fouls: PlayerFoulStats?,
    val cards: PlayerCardStats?,
    val gridPosition:String?
) {
    val hasPlayedTime: Boolean get() = (minutesPlayed ?: 0) > 0
    val ratingFormatted: String get() = rating?.let { "%.1f".format(it) } ?: "N/A"
    val displayNumber: String get() = shirtNumber?.toString() ?: "?"
    val shortPosition: String get() = when (position?.uppercase()) {
        "GOALKEEPER" -> "GK"
        "DEFENDER" -> "DEF"
        "MIDFIELDER" -> "MID"
        "ATTACKER" -> "ATT"
        else -> position?.take(3) ?: "???"
    }

    val performanceColor: Long get() = when {
        rating == null -> 0xFF6B7280
        rating >= 8.0f -> 0xFF10B981  // Excellent - Green
        rating >= 7.0f -> 0xFF3B82F6  // Good - Blue
        rating >= 6.0f -> 0xFFF59E0B  // Average - Yellow
        else -> 0xFFEF4444             // Poor - Red
    }
}

data class PlayerInfo(
    val id: Int,
    val name: String,
    val photo: String?,

) {
    val shortName: String
        get() = name.split(" ").let { parts ->
            when {
                parts.size == 1 -> parts[0]
                parts.size >= 2 -> "${parts[0].first()}. ${parts.last()}"
                else -> name
            }
        }
    val displayName: String get() = name.split(" ").takeLast(1).firstOrNull() ?: name
}

data class PlayerShotStats(
    val total: Int?,
    val onTarget: Int?
) {
    val accuracy: Float?
        get() = if (total != null && total > 0 && onTarget != null) {
            (onTarget.toFloat() / total) * 100
        } else null
}

data class PlayerPassStats(
    val total: Int?,
    val keyPasses: Int?,
    val accuracy: Float?
)

data class PlayerDefenseStats(
    val tackles: Int?,
    val blocks: Int?,
    val interceptions: Int?
) {
    val totalDefensiveActions: Int get() = (tackles ?: 0) + (blocks ?: 0) + (interceptions ?: 0)
}

data class PlayerDuelStats(
    val total: Int?,
    val won: Int?
) {
    val successRate: Float?
        get() = if (total != null && total > 0 && won != null) {
            (won.toFloat() / total) * 100
        } else null
}

data class PlayerDribbleStats(
    val attempts: Int?,
    val successful: Int?
) {
    val successRate: Float?
        get() = if (attempts != null && attempts > 0 && successful != null) {
            (successful.toFloat() / attempts) * 100
        } else null
}

data class PlayerFoulStats(
    val drawn: Int?,
    val committed: Int?
)

data class PlayerCardStats(
    val yellow: Int?,
    val red: Int?
) {
    val totalCards: Int get() = (yellow ?: 0) + (red ?: 0)
}

data class Player(
    val id: Int,
    val name: String,
    val position: String?,
    val shirtNumber: Int
)

/**
 * Model cho đội hình từ API
 */


/**
 * Model cho vị trí cầu thủ trên sân (UI)
 */
data class PlayerPosition(
    val player: PlayerMatchStats,
    val x: Float, // Tỷ lệ từ 0.0 đến 1.0
    val y: Float, // Tỷ lệ từ 0.0 đến 1.0
    val isSubstitute: Boolean = false
)

/**
 * Model cho đội hình UI hoàn chỉnh
 */
data class FormationLayout(
    val name: String,
    val positions: List<PlayerPosition>,
)