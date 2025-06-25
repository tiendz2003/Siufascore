package com.jerry.ronaldo.siufascore.domain.usecase.football

import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLeagueInfoUseCase @Inject constructor(
    private val footballRepository: FootballRepository
) {
     operator fun invoke(competitionId: Int): Flow<Resource<CompetitionLeague>> {
        return footballRepository.getLeagueInfo(competitionId)
    }
}