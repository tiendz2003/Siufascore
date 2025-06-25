package com.jerry.ronaldo.siufascore.domain.usecase.football

import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import javax.inject.Inject

class GetH2HInfoUseCase @Inject constructor(
    private val repository: FootballRepository
) {
    operator fun invoke(homeId: Int, awayId: Int) = repository.getH2HInfo("$homeId-$awayId")
}