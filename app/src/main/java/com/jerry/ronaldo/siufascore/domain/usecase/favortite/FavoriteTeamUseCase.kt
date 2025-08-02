package com.jerry.ronaldo.siufascore.domain.usecase.favortite

import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import com.jerry.ronaldo.siufascore.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AddFavoriteTeamUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(teamInfo: TeamInfo, leagueInfo: LeagueInfo) =
        repository.addFavoriteTeam(
            team = teamInfo,
            league = leagueInfo
        )
}
// Remove Favorite Team Use Case
class RemoveFavoriteTeamUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(teamId: Int): Result<Unit> {
        return repository.removeFavoriteTeam(teamId)
    }
}


// Observe Favorite Teams Use Case
class ObserveFavoriteTeamsUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<List<FavoriteTeam>> {
        return repository.observeFavoriteTeams()
    }
}

// Check if Team is Favorite Use Case
class IsTeamFavoriteUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(teamId: Int): Result<Boolean> {
        return repository.isFavoriteTeam(teamId)
    }
}

class ToggleNotificationUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(teamId: Int, isEnabled: Boolean): Result<Unit> {
        return repository.toggleNotification(teamId, isEnabled)
    }
}


class GetNotificationStatusUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(teamId: Int): Flow<Boolean> {
        return repository.observeFavoriteTeams()
            .map { favoriteTeams ->
                favoriteTeams.find { it.team.id == teamId }?.enableNotification ?: false
            }
            .catch { emit(false) }
    }
}
class GetFavoriteTeamsByLeagueUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<Map<String, List<FavoriteTeam>>> {
        return repository.observeFavoriteTeams()
            .map { favoriteTeams ->
                favoriteTeams.groupBy { it.league.name }
            }
            .catch { emit(emptyMap()) }
    }
}
