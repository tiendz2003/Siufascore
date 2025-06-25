package com.jerry.ronaldo.siufascore.data.repository

import com.jerry.ronaldo.siufascore.data.mapper.toDomain
import com.jerry.ronaldo.siufascore.data.remote.FootballApiService
import com.jerry.ronaldo.siufascore.domain.model.PlayerSearch
import com.jerry.ronaldo.siufascore.domain.model.TeamSearch
import com.jerry.ronaldo.siufascore.domain.repository.SearchRepository
import com.jerry.ronaldo.siufascore.utils.IODispatcher
import com.jerry.ronaldo.siufascore.utils.PaginatedResult
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.handleException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class SearchRepositoryImpl @Inject constructor(
    private val apiService: FootballApiService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : SearchRepository {
    override suspend fun searchTeams(query: String): Flow<Resource<List<TeamSearch>>> = flow {
        emit(Resource.Loading)
        try {
            require(query.length >= 3) {
                "Query phải nhiều hơn 3 ký tự"
            }
            Timber.tag("SearchRepositoryImpl").d("Current thread: ${Thread.currentThread().name}")
            val response = apiService.searchTeams(query)
            val teams = response.toDomain()
            emit(Resource.Success(teams))
        } catch (e: Exception) {
            emit(Resource.Error(e.handleException()))
        }
    }.flowOn(ioDispatcher)

    override suspend fun searchPlayers(
        query: String,
        page: Int
    ): Flow<Resource<PaginatedResult<PlayerSearch>>> = flow {
        emit(Resource.Loading)
        try {
            require(query.length >= 3 && page > 0) {
                "Query phải nhiều hơn 3 ký tự và trang phải lớn hơn 0"
            }
            val response = apiService.searchPlayers(query, page)
            val players = response.toDomain()
            val paginatedResult = PaginatedResult(
                data = players,
                currentPage = response.paging.current,
                totalPages = response.paging.total,
                hasNextPage = response.paging.hasNextPage,
                hasPreviousPage = response.paging.hasPreviousPage,
                totalResults = response.results
            )
            Timber.tag("SearchRepositoryImpl").d("$paginatedResult")

            emit(Resource.Success(paginatedResult))
        } catch (e: Exception) {
            Timber.tag("SearchRepositoryImpl").e("${e.message}")

            emit(Resource.Error(e.handleException()))

        }
    }.flowOn(ioDispatcher)

}