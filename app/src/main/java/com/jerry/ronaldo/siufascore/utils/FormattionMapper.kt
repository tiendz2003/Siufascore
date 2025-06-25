import com.jerry.ronaldo.siufascore.domain.model.FormationLayout
import com.jerry.ronaldo.siufascore.domain.model.PlayerMatchStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerPosition

object FormationMapper {

    /**
     * ✅ Dynamic formation mapping dựa trên số lượng cầu thủ thực tế ở mỗi hàng
     */
    fun mapToFormationLayout(formation: String, lineUp: List<PlayerMatchStats>): FormationLayout {
        // Group players by rows based on their grid position
        val playersByRow = groupPlayersByRow(lineUp)

        val positions = mutableListOf<PlayerPosition>()

        playersByRow.forEach { (row, playersInRow) ->
            val yCoordinate = mapRowToY(row)
            val playerPositions = distributePlayersInRow(playersInRow, yCoordinate, formation)
            positions.addAll(playerPositions)
        }

        return FormationLayout(
            name = formation,
            positions = positions
        )
    }

    /**
     * ✅ Group players by row từ grid position
     */
    private fun groupPlayersByRow(lineUp: List<PlayerMatchStats>): Map<Int, List<PlayerMatchStats>> {
        return lineUp.groupBy { player ->
            player.gridPosition?.split(":")?.get(0)?.toIntOrNull() ?:
            getDefaultRowByPosition(player.position)
        }.toSortedMap()
    }

    /**
     * ✅ Distribute players evenly trong mỗi hàng
     */
    private fun distributePlayersInRow(
        players: List<PlayerMatchStats>,
        yCoordinate: Float,
        formation: String
    ): List<PlayerPosition> {
        if (players.isEmpty()) return emptyList()

        // Sort players by column position (if available) or by position type
        val sortedPlayers = players.sortedWith { p1, p2 ->
            val col1 = p1.gridPosition?.split(":")?.get(1)?.toIntOrNull() ?: getDefaultColumnByPosition(p1.position)
            val col2 = p2.gridPosition?.split(":")?.get(1)?.toIntOrNull() ?: getDefaultColumnByPosition(p2.position)
            col1.compareTo(col2)
        }

        return when (sortedPlayers.size) {
            1 -> {
                // Single player → center
                val player = sortedPlayers[0]
                val x = if (isGoalkeeper(player.position) || isCentralStriker(player.position)) {
                    0.5f // Center for GK and central strikers
                } else {
                    0.5f // Default center
                }
                listOf(PlayerPosition(player, x, yCoordinate, player.isSubstitute))
            }

            2 -> {
                // Two players → balanced
                distributeTwoPlayers(sortedPlayers, yCoordinate)
            }

            3 -> {
                // Three players → left, center, right
                distributeThreePlayers(sortedPlayers, yCoordinate, formation)
            }

            4 -> {
                // Four players → evenly spaced
                distributeFourPlayers(sortedPlayers, yCoordinate, formation)
            }

            5 -> {
                // Five players → full spread
                distributeFivePlayers(sortedPlayers, yCoordinate, formation)
            }

            else -> {
                // More than 5 → compress or use fallback
                distributeMultiplePlayers(sortedPlayers, yCoordinate, formation)
            }
        }
    }

    /**
     * ✅ Distribute 2 players
     */
    private fun distributeTwoPlayers(
        players: List<PlayerMatchStats>,
        yCoordinate: Float
    ): List<PlayerPosition> {
        val spacing = determineSpacingForTwoPlayers(players)
        return listOf(
            PlayerPosition(players[0], spacing.first, yCoordinate, players[0].isSubstitute),
            PlayerPosition(players[1], spacing.second, yCoordinate, players[1].isSubstitute)
        )
    }

    /**
     * ✅ Determine spacing for 2 players based on their positions
     */
    private fun determineSpacingForTwoPlayers(players: List<PlayerMatchStats>): Pair<Float, Float> {
        val pos1 = players[0].position?.uppercase() ?: ""
        val pos2 = players[1].position?.uppercase() ?: ""

        return when {
            // Two center-backs → close together
            pos1.contains("CENTRE-BACK") && pos2.contains("CENTRE-BACK") -> 0.4f to 0.6f

            // Two defensive midfielders → moderately spaced
            pos1.contains("DEFENSIVE") && pos2.contains("DEFENSIVE") -> 0.35f to 0.65f

            // Two central midfielders → standard spacing
            pos1.contains("CENTRAL") && pos2.contains("CENTRAL") -> 0.3f to 0.7f

            // Two strikers → close partnership
            isCentralStriker(pos1) && isCentralStriker(pos2) -> 0.42f to 0.58f

            // Mixed positions → wider spacing
            else -> 0.25f to 0.75f
        }
    }

    /**
     * ✅ Distribute 3 players
     */
    private fun distributeThreePlayers(
        players: List<PlayerMatchStats>,
        yCoordinate: Float,
        formation: String
    ): List<PlayerPosition> {
        val spacing = determineSpacingForThreePlayers(players, formation)
        return listOf(
            PlayerPosition(players[0], spacing[0], yCoordinate, players[0].isSubstitute),
            PlayerPosition(players[1], spacing[1], yCoordinate, players[1].isSubstitute),
            PlayerPosition(players[2], spacing[2], yCoordinate, players[2].isSubstitute)
        )
    }

    /**
     * ✅ Determine spacing for 3 players
     */
    private fun determineSpacingForThreePlayers(
        players: List<PlayerMatchStats>,
        formation: String
    ): FloatArray {
        val positions = players.map { it.position?.uppercase() ?: "" }

        return when {
            // Three center-backs → compact
            positions.all { it.contains("CENTRE-BACK") } -> floatArrayOf(0.3f, 0.5f, 0.7f)

            // Three midfielders → standard
            positions.all { it.contains("MIDFIELD") } -> floatArrayOf(0.25f, 0.5f, 0.75f)

            // Three forwards → attacking spread
            positions.all { it.contains("FORWARD") || it.contains("WINGER") } -> {
                if (formation.contains("4-3-3")) {
                    floatArrayOf(0.15f, 0.5f, 0.85f) // Wide wingers
                } else {
                    floatArrayOf(0.25f, 0.5f, 0.75f) // Standard
                }
            }

            // Mixed → balanced
            else -> floatArrayOf(0.2f, 0.5f, 0.8f)
        }
    }

    /**
     * ✅ Distribute 4 players
     */
    private fun distributeFourPlayers(
        players: List<PlayerMatchStats>,
        yCoordinate: Float,
        formation: String
    ): List<PlayerPosition> {
        val spacing = determineSpacingForFourPlayers(players, formation)
        return players.mapIndexed { index, player ->
            PlayerPosition(player, spacing[index], yCoordinate, player.isSubstitute)
        }
    }

    /**
     * ✅ Determine spacing for 4 players
     */
    private fun determineSpacingForFourPlayers(
        players: List<PlayerMatchStats>,
        formation: String
    ): FloatArray {
        val positions = players.map { it.position?.uppercase() ?: "" }

        return when {
            // Four defenders → classic back 4
            positions.all { it.contains("BACK") } -> floatArrayOf(0.1f, 0.35f, 0.65f, 0.9f)

            // Four midfielders → depends on formation
            positions.all { it.contains("MIDFIELD") || it.contains("WINGER") } -> {
                if (formation.contains("4-4-2")) {
                    floatArrayOf(0.1f, 0.35f, 0.65f, 0.9f) // Flat midfield
                } else {
                    floatArrayOf(0.15f, 0.35f, 0.65f, 0.85f) // More compact
                }
            }

            // Mixed → balanced distribution
            else -> floatArrayOf(0.15f, 0.35f, 0.65f, 0.85f)
        }
    }

    /**
     * ✅ Distribute 5 players
     */
    private fun distributeFivePlayers(
        players: List<PlayerMatchStats>,
        yCoordinate: Float,
        formation: String
    ): List<PlayerPosition> {
        val spacing = when {
            formation.contains("5-") -> floatArrayOf(0.05f, 0.275f, 0.5f, 0.725f, 0.95f) // Five defenders
            formation.contains("3-5-2") -> floatArrayOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f) // Five midfielders
            else -> floatArrayOf(0.1f, 0.3f, 0.5f, 0.7f, 0.9f) // Default
        }

        return players.mapIndexed { index, player ->
            PlayerPosition(player, spacing[index], yCoordinate, player.isSubstitute)
        }
    }

    /**
     * ✅ Handle more than 5 players (rare case)
     */
    private fun distributeMultiplePlayers(
        players: List<PlayerMatchStats>,
        yCoordinate: Float,
        formation: String
    ): List<PlayerPosition> {
        val count = players.size
        val spacing = FloatArray(count) { index ->
            if (count == 1) {
                0.5f
            } else {
                0.1f + (index * (0.8f / (count - 1)))
            }
        }

        return players.mapIndexed { index, player ->
            PlayerPosition(player, spacing[index], yCoordinate, player.isSubstitute)
        }
    }

    /**
     * ✅ Map row to Y coordinate
     */
    private fun mapRowToY(row: Int): Float {
        return when (row) {
            1 -> 0.08f  // Attack
            2 -> 0.25f  // Attacking midfield
            3 -> 0.45f  // Central midfield
            4 -> 0.65f  // Defensive midfield/defense
            5 -> 0.82f  // Defense/goalkeeper
            else -> 0.5f
        }
    }

    /**
     * ✅ Get default row by position (fallback)
     */
    private fun getDefaultRowByPosition(position: String?): Int {
        return when (position?.uppercase()) {
            "G", "GK", "GOALKEEPER" -> 5
            "CENTRE-BACK", "CB", "LEFT-BACK", "RIGHT-BACK", "LB", "RB" -> 4
            "DEFENSIVE MIDFIELD", "DM", "CDM" -> 4
            "CENTRAL MIDFIELD", "CM", "ATTACKING MIDFIELD", "AM" -> 3
            "LEFT WINGER", "RIGHT WINGER", "LW", "RW" -> 2
            "CENTRE-FORWARD", "CF", "ST", "STRIKER" -> 1
            else -> 3
        }
    }

    /**
     * ✅ Get default column by position (for sorting)
     */
    private fun getDefaultColumnByPosition(position: String?): Int {
        return when (position?.uppercase()) {
            "LEFT-BACK", "LB", "LEFT WINGER", "LW" -> 1
            "CENTRE-BACK", "CB", "CENTRAL MIDFIELD", "CM", "CENTRE-FORWARD", "CF" -> 3
            "RIGHT-BACK", "RB", "RIGHT WINGER", "RW" -> 5
            "DEFENSIVE MIDFIELD", "DM", "ATTACKING MIDFIELD", "AM" -> 3
            "G", "GK", "GOALKEEPER" -> 3
            else -> 3
        }
    }

    /**
     * Helper functions
     */
    private fun isGoalkeeper(position: String?): Boolean {
        return position?.uppercase() in listOf("G", "GK", "GOALKEEPER")
    }

    private fun isCentralStriker(position: String?): Boolean {
        return position?.uppercase() in listOf("CF", "ST", "STRIKER", "CENTRE-FORWARD", "CENTER-FORWARD")
    }
}
