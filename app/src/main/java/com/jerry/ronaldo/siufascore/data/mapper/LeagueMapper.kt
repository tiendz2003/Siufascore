package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.LeagueResponse
import com.jerry.ronaldo.siufascore.data.model.LeagueResponse.CurrentSeason
import com.jerry.ronaldo.siufascore.data.model.LeagueResponse.Season
import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.model.SeasonLeague

fun LeagueResponse.toDomain(): CompetitionLeague {
    return CompetitionLeague(
        id = id,
        code = code,
        name = name,
        emblem = emblem,
        areaName = area.name,
        currentSeason = currentSeason.toDomain(),
        availableSeasons = seasons.map { it.toDomain() }
    )
}

private fun CurrentSeason.toDomain(): SeasonLeague {
    return SeasonLeague(
        id = id,
        startDate = startDate,
        endDate = endDate,
        currentMatchday = currentMatchday,

        )
}

private fun Season.toDomain(): SeasonLeague {
    return SeasonLeague(
        id = id,
        startDate = startDate,
        endDate = endDate,
        currentMatchday = currentMatchday ?: 0, // Xử lý nullable
    )
}

