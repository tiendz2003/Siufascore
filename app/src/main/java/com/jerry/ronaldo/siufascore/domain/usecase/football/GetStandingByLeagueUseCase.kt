package com.jerry.ronaldo.siufascore.domain.usecase.football

import com.jerry.ronaldo.siufascore.domain.model.LeagueStandings
import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStandingByLeagueUseCase @Inject constructor(
    private val repository: FootballRepository
) {
    operator fun invoke(
        competitionId: Int,
    ): Flow<Resource<List<LeagueStandings>>> {
        return repository.getStandingByLeague(competitionId)
    }
}
