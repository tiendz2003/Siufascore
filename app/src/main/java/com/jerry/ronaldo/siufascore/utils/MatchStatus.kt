package com.jerry.ronaldo.siufascore.utils

enum class MatchStatus(val displayName: String) {
    SCHEDULED("Sắp diễn ra"),
    TIMED("Đã lên lịch"),
    POSTPONED("Bị hoãn"),
    IN_PLAY("Đang thi đấu"),
    PAUSED("Nghỉ giữa hiệp"),
    FINISHED("Kết thúc"),
    SUSPENDED("Tạm dừng"),
    CANCELLED("Đã hủy");

    companion object {
        fun from(status: String?): MatchStatus? {
            return status?.let { valueOf(it.uppercase()) }
        }
    }
}


enum class WinnerSide {
    HOME, AWAY, DRAW, NONE
}