package com.jerry.ronaldo.siufascore.domain.repository

import androidx.annotation.WorkerThread
import com.jerry.ronaldo.siufascore.data.model.ResponseTeamStatistics
import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.model.DetailMatch
import com.jerry.ronaldo.siufascore.domain.model.LeagueStandings
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.PlayerStatistics
import com.jerry.ronaldo.siufascore.domain.model.PlayerTeam
import com.jerry.ronaldo.siufascore.domain.model.PlayerTrophy
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FootballRepository {
    @WorkerThread
    fun getMatchByLeague(competitionId: Int, matchday: String): Flow<Resource<List<Match>>>

    @WorkerThread
    fun getStandingByLeague(
        competitionId: Int,
        season: Int = 2025
    ): Flow<Resource<List<LeagueStandings>>>

    @WorkerThread
    fun getLeagueInfo(competitionId: Int): Flow<Resource<CompetitionLeague>>

    @WorkerThread
    fun getDetailMatchInfo(fixtureId: Int): Flow<Resource<DetailMatch>>

    @WorkerThread
    fun getH2HInfo(matchIds: String): Flow<Resource<List<Match>>>

    @WorkerThread
    fun getDetailPlayerInfo(playerId: Int, season: Int): Flow<Resource<PlayerStatistics>>

    @WorkerThread
    suspend fun getPlayerTeams(playerId: Int): Flow<Resource<List<PlayerTeam>>>

    @WorkerThread
    suspend fun getPlayerTrophies(playerId: Int): Flow<Resource<List<PlayerTrophy>>>

    @WorkerThread
    suspend fun getDetailTeamInfo(teamId: Int,leagueId:Int, season: Int): Flow<Resource<ResponseTeamStatistics>>
}