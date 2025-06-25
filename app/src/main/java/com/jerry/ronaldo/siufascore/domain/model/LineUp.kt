package com.jerry.ronaldo.siufascore.domain.model

data class Player(
    val id: Int,
    val name: String,
    val position: String?,
    val shirtNumber: Int
)

/**
 * Model cho đội hình từ API
 */
data class TeamLineup(
    val formation: String, // "4-2-3-1", "4-3-3", etc.
    val lineup: List<Player>,
    val bench: List<Player>
)

/**
 * Model cho vị trí cầu thủ trên sân (UI)
 */
data class PlayerPosition(
    val player: Player,
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
    val bench: List<Player>
)