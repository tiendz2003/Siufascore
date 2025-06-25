package com.jerry.ronaldo.siufascore.domain.usecase

import com.jerry.ronaldo.siufascore.domain.model.LeagueStanding
import com.jerry.ronaldo.siufascore.domain.repository.MatchRepository
import com.jerry.ronaldo.siufascore.presentation.mapper.mapToLeagueTable
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetStandingByLeagueUseCase @Inject constructor(
    private val repository: MatchRepository
) {
    operator fun invoke(
        competitionId: String,
        matchday: Int
    ): Flow<Resource<LeagueStanding>> {
        return repository.getStandingByLeague(competitionId, matchday).map { resource ->
            resource.map {
                it.mapToLeagueTable()
            }
        }
    }
}