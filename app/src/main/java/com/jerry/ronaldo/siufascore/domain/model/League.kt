package com.jerry.ronaldo.siufascore.domain.model

data class CompetitionLeague(
    val leagueName: String,
    val season: String,
    val currentMatchday: List<String>,
)
