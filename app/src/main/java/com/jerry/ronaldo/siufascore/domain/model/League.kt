package com.jerry.ronaldo.siufascore.domain.model

data class CompetitionLeague(
    val id: Int,
    val code: String,
    val name: String,
    val emblem: String,
    val areaName: String,
    val currentSeason: SeasonLeague,
    val availableSeasons: List<SeasonLeague>
)

data class SeasonLeague(
    val id: Int,
    val startDate: String,
    val endDate: String,
    val currentMatchday: Int,
)
