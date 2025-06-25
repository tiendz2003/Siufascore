package com.jerry.ronaldo.siufascore.data.repository

import com.jerry.ronaldo.siufascore.data.mapper.toDomain
import com.jerry.ronaldo.siufascore.data.mapper.toDomainMatchList
import com.jerry.ronaldo.siufascore.data.remote.FootBallApiService
import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.StandingData
import com.jerry.ronaldo.siufascore.domain.repository.MatchRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val apiService: FootBallApiService
) : MatchRepository {
    override fun getMatchByLeague(
        competitionId: String,
        matchday: Int
    ): Flow<Resource<List<Match>>> {
        return flow {
            emit(Resource.Loading)
            try {
                val response = apiService.getMatchByLeague(competitionId, matchday)
                Timber.tag("MatchRepositoryImpl").d("$response")
                emit(Resource.Success(response.toDomainMatchList()))
            } catch (e: Exception) {
                emit(Resource.Error(handleException(e)))
            }
        }
    }

    override  fun getStandingByLeague(
        competitionId: String,
        matchday: Int
    ): Flow<Resource<StandingData>> {
        return flow {
            emit(Resource.Loading)
            try {
                val response = apiService.getStandingByLeague(competitionId, matchday)
                Timber.tag("MatchRepositoryImpl").d("$response")
                emit(Resource.Success(response.toDomain()))
            } catch (e: Exception) {
                emit(Resource.Error(handleException(e)))
            }
        }
    }

    override fun getLeagueInfo(competitionId: String): Flow<Resource<CompetitionLeague>> {
        return flow {
            emit(Resource.Loading)
            try {
                val result = apiService.getCurrentSeasonInfo(competitionId)
                emit(Resource.Success(result.toDomain()))
            } catch (e: Exception) {
                emit(Resource.Error(handleException(e)))
            }
        }
    }

    private fun handleException(e: Throwable): Exception {
        return when (e) {
            is HttpException -> {
                when (e.code()) {
                    401 -> Exception("Không có quyền truy cập")
                    404 -> Exception("Không tìm thấy dữ liệu")
                    500 -> Exception("Lỗi sever")
                    else -> Exception("API Error: ${e.code()}")
                }
            }

            is IOException -> Exception("Lỗi mạng:${e.localizedMessage}")
            else -> Exception("Lỗi không rõ:${e.localizedMessage}")
        }
    }
}