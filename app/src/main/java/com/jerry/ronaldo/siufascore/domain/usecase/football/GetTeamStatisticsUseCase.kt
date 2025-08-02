package com.jerry.ronaldo.siufascore.domain.usecase.football

import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import javax.inject.Inject

class GetTeamStatisticsUseCase @Inject constructor(
    private val footballRepository:FootballRepository
) {
    suspend operator fun invoke (teamId:Int,leagueId:Int,season:Int,) = footballRepository.getTeamStatistics(
        teamId = teamId,
        leagueId = leagueId,
        season = season,
    )
}