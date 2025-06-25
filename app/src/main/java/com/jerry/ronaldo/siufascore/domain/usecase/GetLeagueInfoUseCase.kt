package com.jerry.ronaldo.siufascore.domain.usecase

import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.repository.MatchRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLeagueInfoUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
     operator fun invoke(competitionId: String): Flow<Resource<CompetitionLeague>> {
        return matchRepository.getLeagueInfo(competitionId)
    }
}