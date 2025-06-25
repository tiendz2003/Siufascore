package com.jerry.ronaldo.siufascore.presentation.search

import androidx.annotation.Keep

@Keep
enum class SearchType(val displayName:String) {
    TEAMS("Câu lạc bộ"),
    PLAYERS("Cầu thủ");
    companion object {
        val DEFAULT = TEAMS
    }
}
