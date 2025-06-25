package com.jerry.ronaldo.siufascore.domain.usecase.football

import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMatchesByLeagueUseCase @Inject constructor(
    private val footballRepository:FootballRepository
) {
      operator fun invoke(competitionId:Int, matchDay:String): Flow<Resource<List<Match>>>{
        return footballRepository.getMatchByLeague(competitionId,matchDay)
    }
}