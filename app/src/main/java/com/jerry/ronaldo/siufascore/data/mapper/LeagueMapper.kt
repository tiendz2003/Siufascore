package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.CurrentRoundResponse
import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague

fun CurrentRoundResponse.toDomain(): CompetitionLeague {
    return CompetitionLeague(
        leagueName = this.parameters.league,
        season = this.parameters.season,
        currentMatchday = this.response
    )
}
