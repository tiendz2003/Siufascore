package com.jerry.ronaldo.siufascore.domain.repository

import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.StandingData
import com.jerry.ronaldo.siufascore.utils.Resource

interface MatchRepository {
    suspend fun getMatchByLeague(competitionId: String, matchday: Int): Resource<List<Match>>
    suspend fun getStandingByLeague(competitionId: String, matchday: Int): Resource<StandingData>
    suspend fun getLeagueInfo(competitionId: String): Resource<CompetitionLeague>
}