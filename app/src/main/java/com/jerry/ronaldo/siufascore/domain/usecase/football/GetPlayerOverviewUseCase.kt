package com.jerry.ronaldo.siufascore.domain.usecase.football

import com.jerry.ronaldo.siufascore.domain.model.PlayerOverview
import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetPlayerOverviewUseCase @Inject constructor(
    private val footballRepository: FootballRepository
) {
    suspend operator fun invoke(playerId: Int): Flow<Resource<PlayerOverview>> {
        val teams = footballRepository.getPlayerTeams(playerId)
        val trophies = footballRepository.getPlayerTrophies(playerId)
        return combine(teams, trophies) { teamsResource, trophiesResource ->
            when {
                teamsResource is Resource.Loading || trophiesResource is Resource.Loading -> {
                    Resource.Loading
                }

                teamsResource is Resource.Error -> teamsResource
                trophiesResource is Resource.Error -> trophiesResource
                teamsResource is Resource.Success && trophiesResource is Resource.Success -> {
                    Resource.Success(
                        PlayerOverview(
                            playerId = playerId,
                            teams = teamsResource.data,
                            trophies = trophiesResource.data
                        )
                    )
                }

                else -> Resource.Error(Exception("Lỗi không rõ"))
            }
        }.catch { e ->
            emit(Resource.Error(Exception("Lỗi khi kết hợp dữ liệu: ${e.message}")))
        }
    }
}
