package com.jerry.ronaldo.siufascore.domain.repository

import com.jerry.ronaldo.siufascore.domain.model.PlayerSearch
import com.jerry.ronaldo.siufascore.domain.model.TeamSearch
import com.jerry.ronaldo.siufascore.utils.PaginatedResult
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    suspend fun searchTeams(query: String): Flow<Resource<List<TeamSearch>>>
    suspend fun searchPlayers(query: String, page: Int): Flow<Resource<PaginatedResult<PlayerSearch>>>
}