package com.jerry.ronaldo.siufascore.data.repository

import com.jerry.ronaldo.siufascore.data.mapper.toDomain
import com.jerry.ronaldo.siufascore.data.mapper.toDomainMatchList
import com.jerry.ronaldo.siufascore.data.remote.FootBallApiService
import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.StandingData
import com.jerry.ronaldo.siufascore.domain.repository.MatchRepository
import com.jerry.ronaldo.siufascore.utils.Resource
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MatchRepositoryImpl @Inject constructor(
    private val apiService: FootBallApiService
) : MatchRepository {
    override suspend fun getMatchByLeague(
        competitionId: String,
        matchday: Int
    ): Resource<List<Match>> {
        return try {
            val response = apiService.getMatchByLeague(competitionId, matchday)
            Timber.tag("MatchRepositoryImpl").d("$response")
            Resource.Success(response.toDomainMatchList())
        } catch (e: Exception) {
            Resource.Error(handleException(e))
        }
    }

    override suspend fun getStandingByLeague(
        competitionId: String,
        matchday: Int
    ): Resource<StandingData> {
        return try {
            val response = apiService.getStandingByLeague(competitionId, matchday)
            Timber.tag("MatchRepositoryImpl").d("$response")
            Resource.Success(response.toDomain())
        } catch (e: Exception) {
            Resource.Error(handleException(e))
        }
    }

    override suspend fun getLeagueInfo(competitionId: String): Resource<CompetitionLeague> {
        return try {
            val result = apiService.getCurrentSeasonInfo(competitionId)
            Resource.Success(result.toDomain())
        } catch (e: Exception) {
            Resource.Error(handleException(e))
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