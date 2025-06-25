package com.jerry.ronaldo.siufascore.domain.usecase

import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.repository.MatchRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMatchesByLeagueUseCase @Inject constructor(
    private val matchRepository:MatchRepository
) {
      operator fun invoke(competitionId:String, matchDay:Int): Flow<Resource<List<Match>>>{
        return matchRepository.getMatchByLeague(competitionId,matchDay)
    }
}