package com.jerry.ronaldo.siufascore.domain.usecase.football

import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import javax.inject.Inject

class GetDetailPlayerUseCase @Inject constructor(
    private val repository: FootballRepository
) {
    operator fun invoke(playerId: Int, season: Int) =
        repository.getDetailPlayerInfo(playerId, season)
}