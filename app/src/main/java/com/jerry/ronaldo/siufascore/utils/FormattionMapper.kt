package com.jerry.ronaldo.siufascore.utils

import com.jerry.ronaldo.siufascore.domain.model.FormationLayout
import com.jerry.ronaldo.siufascore.domain.model.Player
import com.jerry.ronaldo.siufascore.domain.model.PlayerPosition
import com.jerry.ronaldo.siufascore.domain.model.TeamLineup

object FormationMapper {

    /**
     * Chuyển đổi từ TeamLineup (API) sang FormationLayout (UI)
     */
    fun mapToFormationLayout(teamLineup: TeamLineup): FormationLayout {
        val positions = when (teamLineup.formation) {
            "4-2-3-1" -> map4231Formation(teamLineup.lineup)
            "4-3-3" -> map433Formation(teamLineup.lineup)
            "4-4-2" -> map442Formation(teamLineup.lineup)
            "3-5-2" -> map352Formation(teamLineup.lineup)
            else -> mapGenericFormation(teamLineup.lineup, teamLineup.formation)
        }

        return FormationLayout(
            name = teamLineup.formation,
            positions = positions,
            bench = teamLineup.bench
        )
    }

    /**
     * Mapping cho đội hình 4-2-3-1
     */
    private fun map4231Formation(players: List<Player>): List<PlayerPosition> {
        val positionMap = mutableMapOf<String, PlayerPosition>()

        players.forEach { player ->
            val (x, y) = when (player.position) {
                "Goalkeeper" -> 0.5f to 0.85f

                // Hàng thủ
                "Left-Back" -> 0.15f to 0.65f
                "Centre-Back" -> {
                    if (positionMap.values.none { it.player.position == "Centre-Back" }) {
                        0.35f to 0.65f // CB trái
                    } else {
                        0.65f to 0.65f // CB phải
                    }
                }
                "Right-Back" -> 0.85f to 0.65f

                // Tiền vệ phòng ngự (2 người)
                "Defensive Midfield", "Central Midfield" -> {
                    val existingDM = positionMap.values.count {
                        it.player.position in listOf("Defensive Midfield", "Central Midfield")
                    }
                    when (existingDM) {
                        0 -> 0.35f to 0.45f // DM trái
                        else -> 0.65f to 0.45f // DM phải
                    }
                }

                // Tiền vệ tấn công (3 người)
                "Left Winger" -> 0.15f to 0.25f
                "Attacking Midfield" -> 0.5f to 0.25f
                "Right Winger" -> 0.85f to 0.25f

                // Tiền đạo
                "Centre-Forward" -> 0.5f to 0.1f

                else -> 0.5f to 0.5f // Vị trí mặc định
            }

            positionMap[player.name] = PlayerPosition(player, x, y)
        }

        return positionMap.values.toList()
    }

    /**
     * Mapping cho đội hình 4-3-3
     */
    private fun map433Formation(players: List<Player>): List<PlayerPosition> {
        val positionMap = mutableMapOf<String, PlayerPosition>()

        players.forEach { player ->
            val (x, y) = when (player.position) {
                "Goalkeeper" -> 0.5f to 0.85f

                // Hàng thủ
                "Left-Back" -> 0.15f to 0.65f
                "Centre-Back" -> {
                    if (positionMap.values.none { it.player.position == "Centre-Back" }) {
                        0.35f to 0.65f
                    } else {
                        0.65f to 0.65f
                    }
                }
                "Right-Back" -> 0.85f to 0.65f

                // Tiền vệ (3 người)
                "Defensive Midfield" -> 0.5f to 0.45f
                "Central Midfield" -> {
                    val existingCM = positionMap.values.count { it.player.position == "Central Midfield" }
                    when (existingCM) {
                        0 -> 0.25f to 0.35f
                        else -> 0.75f to 0.35f
                    }
                }
                "Attacking Midfield" -> 0.5f to 0.35f

                // Hàng công (3 người)
                "Left Winger" -> 0.15f to 0.15f
                "Centre-Forward" -> 0.5f to 0.1f
                "Right Winger" -> 0.85f to 0.15f

                else -> 0.5f to 0.5f
            }

            positionMap[player.name] = PlayerPosition(player, x, y)
        }

        return positionMap.values.toList()
    }

    /**
     * Mapping cho đội hình 4-4-2
     */
    private fun map442Formation(players: List<Player>): List<PlayerPosition> {
        val positionMap = mutableMapOf<String, PlayerPosition>()

        players.forEach { player ->
            val (x, y) = when (player.position) {
                "Goalkeeper" -> 0.5f to 0.85f

                // Hàng thủ
                "Left-Back" -> 0.15f to 0.65f
                "Centre-Back" -> {
                    if (positionMap.values.none { it.player.position == "Centre-Back" }) {
                        0.35f to 0.65f
                    } else {
                        0.65f to 0.65f
                    }
                }
                "Right-Back" -> 0.85f to 0.65f

                // Tiền vệ (4 người)
                "Left Winger" -> 0.15f to 0.4f
                "Central Midfield", "Defensive Midfield" -> {
                    val existingMid = positionMap.values.count {
                        it.player.position in listOf("Central Midfield", "Defensive Midfield")
                    }
                    when (existingMid) {
                        0 -> 0.35f to 0.4f
                        else -> 0.65f to 0.4f
                    }
                }
                "Right Winger" -> 0.85f to 0.4f
                "Attacking Midfield" -> 0.5f to 0.4f

                // Tiền đạo (2 người)
                "Centre-Forward" -> {
                    val existingCF = positionMap.values.count { it.player.position == "Centre-Forward" }
                    when (existingCF) {
                        0 -> 0.4f to 0.15f
                        else -> 0.6f to 0.15f
                    }
                }

                else -> 0.5f to 0.5f
            }

            positionMap[player.name] = PlayerPosition(player, x, y)
        }

        return positionMap.values.toList()
    }

    /**
     * Mapping cho đội hình 3-5-2
     */
    private fun map352Formation(players: List<Player>): List<PlayerPosition> {
        val positionMap = mutableMapOf<String, PlayerPosition>()

        players.forEach { player ->
            val (x, y) = when (player.position) {
                "Goalkeeper" -> 0.5f to 0.85f

                // Hàng thủ (3 người)
                "Centre-Back" -> {
                    val existingCB = positionMap.values.count { it.player.position == "Centre-Back" }
                    when (existingCB) {
                        0 -> 0.25f to 0.65f // CB trái
                        1 -> 0.5f to 0.65f  // CB giữa
                        else -> 0.75f to 0.65f // CB phải
                    }
                }
                "Left-Back" -> 0.15f to 0.5f // Wing-back trái
                "Right-Back" -> 0.85f to 0.5f // Wing-back phải

                // Tiền vệ (5 người)
                "Left Winger" -> 0.15f to 0.35f
                "Central Midfield", "Defensive Midfield" -> {
                    val existingMid = positionMap.values.count {
                        it.player.position in listOf("Central Midfield", "Defensive Midfield")
                    }
                    when (existingMid) {
                        0 -> 0.35f to 0.4f
                        1 -> 0.5f to 0.35f
                        else -> 0.65f to 0.4f
                    }
                }
                "Attacking Midfield" -> 0.5f to 0.25f
                "Right Winger" -> 0.85f to 0.35f

                // Tiền đạo (2 người)
                "Centre-Forward" -> {
                    val existingCF = positionMap.values.count { it.player.position == "Centre-Forward" }
                    when (existingCF) {
                        0 -> 0.4f to 0.15f
                        else -> 0.6f to 0.15f
                    }
                }

                else -> 0.5f to 0.5f
            }

            positionMap[player.name] = PlayerPosition(player, x, y)
        }

        return positionMap.values.toList()
    }

    /**
     * Mapping generic cho các đội hình khác
     */
    private fun mapGenericFormation(players: List<Player>, formation: String): List<PlayerPosition> {
        // Phân tích formation string để tự động sắp xếp
        return players.mapIndexed { index, player ->
            val (x, y) = when (player.position) {
                "Goalkeeper" -> 0.5f to 0.85f
                "Left-Back" -> 0.15f to 0.65f
                "Centre-Back" -> if (index % 2 == 0) 0.35f to 0.65f else 0.65f to 0.65f
                "Right-Back" -> 0.85f to 0.65f
                "Defensive Midfield" -> 0.5f to 0.5f
                "Central Midfield" -> if (index % 2 == 0) 0.3f to 0.4f else 0.7f to 0.4f
                "Attacking Midfield" -> 0.5f to 0.3f
                "Left Winger" -> 0.15f to 0.25f
                "Right Winger" -> 0.85f to 0.25f
                "Centre-Forward" -> 0.5f to 0.15f
                else -> 0.5f to 0.5f
            }
            PlayerPosition(player, x, y)
        }
    }
}