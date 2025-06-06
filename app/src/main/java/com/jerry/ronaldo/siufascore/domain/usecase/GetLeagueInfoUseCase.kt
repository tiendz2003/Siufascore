package com.jerry.ronaldo.siufascore.domain.usecase

import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.repository.MatchRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import javax.inject.Inject

class GetLeagueInfoUseCase @Inject constructor(
    private val matchRepository: MatchRepository
) {
    suspend operator fun invoke(competitionId: String): Resource<CompetitionLeague> {
        return matchRepository.getLeagueInfo(competitionId)
    }
}