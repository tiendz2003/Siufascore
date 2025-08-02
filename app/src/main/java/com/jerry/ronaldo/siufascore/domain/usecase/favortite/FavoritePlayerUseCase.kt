package com.jerry.ronaldo.siufascore.domain.usecase.favortite

import com.jerry.ronaldo.siufascore.data.model.FavoritePlayer
import com.jerry.ronaldo.siufascore.domain.repository.FavoriteRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddFavoritePlayerUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(player: FavoritePlayer): Result<Unit> {
        return repository.addFavoritePlayer(player)
    }
}

class RemoveFavoritePlayerUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(playerId: String): Result<Unit> {
        return repository.removeFavoritePlayer(playerId)
    }
}



class IsFavoritePlayerUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(playerId: String): Result<Boolean> {
        return repository.isFavoritePlayer(playerId)
    }
}

class ObserveFavoritePlayersUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    operator fun invoke(): Flow<Resource<List<FavoritePlayer>>> {
        return repository.observeFavoritePlayers()
    }
}

class TogglePlayerNotificationUseCase @Inject constructor(
    private val repository: FavoriteRepository
) {
    suspend operator fun invoke(playerId: String, isEnabled: Boolean): Result<Unit> {
        return repository.togglePlayerNotification(playerId, isEnabled)
    }
}