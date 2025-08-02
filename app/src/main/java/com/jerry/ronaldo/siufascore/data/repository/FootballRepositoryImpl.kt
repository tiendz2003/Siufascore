package com.jerry.ronaldo.siufascore.data.repository

import com.jerry.ronaldo.siufascore.data.mapper.mapToDomain
import com.jerry.ronaldo.siufascore.data.mapper.toDomain
import com.jerry.ronaldo.siufascore.data.mapper.toDomainMatchList
import com.jerry.ronaldo.siufascore.data.mapper.toDomainModel
import com.jerry.ronaldo.siufascore.data.model.ResponseTeamStatistics
import com.jerry.ronaldo.siufascore.data.remote.FootballApiService
import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.model.DetailMatch
import com.jerry.ronaldo.siufascore.domain.model.LeagueStandings
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.PlayerStatistics
import com.jerry.ronaldo.siufascore.domain.model.PlayerTeam
import com.jerry.ronaldo.siufascore.domain.model.PlayerTrophy
import com.jerry.ronaldo.siufascore.domain.repository.FootballRepository
import com.jerry.ronaldo.siufascore.utils.IODispatcher
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.handleException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FootballRepositoryImpl @Inject constructor(
    private val apiService: FootballApiService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : FootballRepository {
    override fun getMatchByLeague(
        competitionId: Int,
        matchday: String
    ): Flow<Resource<List<Match>>> {
        return flow {
            emit(Resource.Loading)
            try {
                val response = apiService.getMatchByLeague(
                    leagueId = competitionId,
                    round = matchday,
                )
                Timber.tag("MatchRepositoryImpl").d("$response")
                emit(Resource.Success(response.toDomainMatchList()))
            } catch (e: Exception) {
                emit(Resource.Error(e.handleException()))
            }
        }.flowOn(ioDispatcher)
    }

    override fun getStandingByLeague(
        competitionId: Int,
        season: Int
    ): Flow<Resource<List<LeagueStandings>>> {
        return flow {
            emit(Resource.Loading)
            try {
                val response = apiService.getStandingByLeague(competitionId, season)
                Timber.tag("MatchRepositoryImpl").d("$response")
                emit(Resource.Success(response.toDomain()))
            } catch (e: Exception) {
                emit(Resource.Error(e.handleException()))
            }
        }.flowOn(ioDispatcher)
    }

    override fun getLeagueInfo(competitionId: Int): Flow<Resource<CompetitionLeague>> {
        return flow {
            emit(Resource.Loading)
            try {
                val result = apiService.getCurrentSeasonInfo(competitionId)
                emit(Resource.Success(result.toDomain()))
            } catch (e: Exception) {
                emit(Resource.Error(e.handleException()))
            }
        }
    }

    override fun getDetailMatchInfo(fixtureId: Int): Flow<Resource<DetailMatch>> {
        return flow {
            emit(Resource.Loading)
            try {
                val result = apiService.getDetailMatch(fixtureId)
                val matchDetail = result.toDomain()
                if (matchDetail != null) {
                    emit(Resource.Success(matchDetail))
                } else {
                    emit(Resource.Error(Exception("Không tìm thấy chi tiết trận đấu")))
                }
            } catch (e: Exception) {
                emit(Resource.Error(e.handleException()))
            }
        }.flowOn(ioDispatcher)
    }

    override fun getH2HInfo(matchIds: String): Flow<Resource<List<Match>>> {
        return flow {
            emit(Resource.Loading)
            try {
                val result = apiService.getHeadToHead(matchIds)
                val matchDetail = result.toDomainMatchList()
                emit(Resource.Success(matchDetail))
            } catch (e: Exception) {
                emit(Resource.Error(e.handleException()))
            }
        }.flowOn(ioDispatcher)
    }

    override fun getDetailPlayerInfo(playerId: Int, season: Int): Flow<Resource<PlayerStatistics>> =
        flow {
            emit(Resource.Loading)
            try {
                require(playerId > 0) { " phải > 0 " }
                require(season > 1990) { "Mùa giải không hợp lệ" }
                val response = apiService.getDetailPlayerInfo(playerId, season)
                val playerStats = response.mapToDomain()
                emit(Resource.Success(playerStats))
            } catch (e: Exception) {
                emit(Resource.Error(e.handleException()))
            }
        }.flowOn(ioDispatcher)

    override suspend fun getPlayerTeams(playerId: Int): Flow<Resource<List<PlayerTeam>>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getPlayerTeams(playerId)
            if (response.errors.isNotEmpty() && response.errors.any { !it.isNullOrBlank() }) {
                emit(Resource.Error(Exception(response.errors.joinToString())))
                return@flow
            }
            val playerTeams = response.toDomainModel()
            emit(Resource.Success(playerTeams))
        } catch (e: Exception) {
            emit(Resource.Error(e.handleException()))
        }
    }.flowOn(ioDispatcher)

    override suspend fun getPlayerTrophies(playerId: Int): Flow<Resource<List<PlayerTrophy>>> =
        flow {
            emit(Resource.Loading)
            try {
                val response = apiService.getPlayerTrophies(playerId)
                if (response.errors.isNotEmpty() && response.errors.any { !it.isNullOrBlank() }) {
                    emit(Resource.Error(Exception(response.errors.joinToString())))
                    return@flow
                }
                val playerTeams = response.toDomainModel()
                emit(Resource.Success(playerTeams))
            } catch (e: Exception) {
                emit(Resource.Error(e.handleException()))
            }
        }.flowOn(ioDispatcher)

    override suspend fun getTeamStatistics(
        teamId: Int,
        leagueId:Int,
        season: Int
    ): Flow<Resource<ResponseTeamStatistics>> {
        return flow {
            emit(Resource.Loading)
            try {
                val response = apiService.getTeamStatistics(
                    leagueId = leagueId,
                    teamId = teamId,
                    season = season
                )
                val teamStatistic = response.response
                emit(Resource.Success(teamStatistic))
            } catch (e: Exception) {
                emit(Resource.Error(e.handleException()))
            }
        }.flowOn(ioDispatcher)
    }


}

