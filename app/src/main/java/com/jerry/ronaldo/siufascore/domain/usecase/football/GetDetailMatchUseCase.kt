package com.jerry.ronaldo.siufascore.domain.usecase.football

import com.jerry.ronaldo.siufascore.domain.model.DetailMatch
import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDetailMatchUseCase @Inject constructor(
    private val repository: FootballRepository
) {
    operator fun invoke(matchId:Int):Flow<Resource<DetailMatch>>{
        return repository.getDetailMatchInfo(matchId)
    }
}