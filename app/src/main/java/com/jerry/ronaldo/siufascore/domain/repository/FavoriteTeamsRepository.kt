package com.jerry.ronaldo.siufascore.domain.repository

import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.domain.model.LeagueInfo
import com.jerry.ronaldo.siufascore.domain.model.TeamInfo
import kotlinx.coroutines.flow.Flow

interface FavoriteTeamsRepository {
    suspend fun addFavoriteTeam(team: TeamInfo, league: LeagueInfo): Result<Unit>
    suspend fun removeFavoriteTeam(teamId: Int): Result<Unit>
    suspend fun getFavoriteTeams(): Result<List<FavoriteTeam>>
    suspend fun isFavoriteTeam(teamId: Int): Result<Boolean>
    suspend fun toggleNotification(teamId: Int, isEnabled: Boolean): Result<Unit>
    fun observeFavoriteTeams(): Flow<List<FavoriteTeam>>
}