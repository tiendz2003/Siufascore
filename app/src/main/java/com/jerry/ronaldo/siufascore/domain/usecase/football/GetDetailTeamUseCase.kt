package com.jerry.ronaldo.siufascore.domain.usecase.football

import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import javax.inject.Inject

class GetDetailTeamUseCase @Inject constructor(
    private val footballRepository:FootballRepository
) {
    suspend operator fun invoke (teamId:Int,leagueId:Int,season:Int,) = footballRepository.getDetailTeamInfo(
        teamId = teamId,
        leagueId = leagueId,
        season = season,
    )
}