package com.jerry.ronaldo.siufascore.domain.usecase.favortite

import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import com.jerry.ronaldo.siufascore.domain.repository.FavoriteTeamsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddFavoriteTeamUseCase @Inject constructor(
    private val repository: FavoriteTeamsRepository
) {
    suspend operator fun invoke(teamInfo: TeamInfo, leagueInfo: LeagueInfo) =
        repository.addFavoriteTeam(
            team = teamInfo,
            league = leagueInfo
        )
}
// Remove Favorite Team Use Case
class RemoveFavoriteTeamUseCase @Inject constructor(
    private val repository: FavoriteTeamsRepository
) {
    suspend operator fun invoke(teamId: Int): Result<Unit> {
        return repository.removeFavoriteTeam(teamId)
    }
}

// Get Favorite Teams Use Case
class GetFavoriteTeamsUseCase @Inject constructor(
    private val repository: FavoriteTeamsRepository
) {
    suspend operator fun invoke(): Result<List<FavoriteTeam>> {
        return repository.getFavoriteTeams()
    }
}

// Observe Favorite Teams Use Case
class ObserveFavoriteTeamsUseCase @Inject constructor(
    private val repository: FavoriteTeamsRepository
) {
    operator fun invoke(): Flow<List<FavoriteTeam>> {
        return repository.observeFavoriteTeams()
    }
}

// Check if Team is Favorite Use Case
class IsTeamFavoriteUseCase @Inject constructor(
    private val repository: FavoriteTeamsRepository
) {
    suspend operator fun invoke(teamId: Int): Result<Boolean> {
        return repository.isFavoriteTeam(teamId)
    }
}