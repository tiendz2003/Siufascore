package com.jerry.ronaldo.siufascore.domain.usecase

import com.jerry.ronaldo.siufascore.domain.model.LeagueStanding
import com.jerry.ronaldo.siufascore.domain.repository.MatchRepository
import com.jerry.ronaldo.siufascore.presentation.mapper.mapToLeagueTable
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.map
import javax.inject.Inject

class GetStandingByLeagueUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    suspend operator fun invoke(
        competitionId: String,
        matchday: Int
    ): Resource<LeagueStanding> {
        return repository.getStandingByLeague(competitionId, matchday).map {
            it.mapToLeagueTable()
        }
    }
}