package com.jerry.ronaldo.siufascore.domain.repository

import com.jerry.ronaldo.siufascore.data.model.FavoritePlayer
import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    suspend fun addFavoriteTeam(team: TeamInfo, league: LeagueInfo): Result<Unit>
    suspend fun removeFavoriteTeam(teamId: Int): Result<Unit>
    suspend fun isFavoriteTeam(teamId: Int): Result<Boolean>
    suspend fun toggleNotification(teamId: Int, isEnabled: Boolean): Result<Unit>
    fun observeFavoriteTeams(): Flow<List<FavoriteTeam>>

    suspend fun addFavoritePlayer(player: FavoritePlayer): Result<Unit>
    suspend fun removeFavoritePlayer(playerId: String): Result<Unit>
    suspend fun isFavoritePlayer(playerId: String): Result<Boolean>
    suspend fun togglePlayerNotification(playerId: String, isEnabled: Boolean): Result<Unit>
    fun observeFavoritePlayers(): Flow<Resource<List<FavoritePlayer>>>
}