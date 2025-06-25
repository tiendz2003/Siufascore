package com.jerry.ronaldo.siufascore.domain.repository

import com.jerry.ronaldo.siufascore.domain.model.CompetitionLeague
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.StandingData
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow

interface MatchRepository {
     fun getMatchByLeague(competitionId: String, matchday: Int): Flow<Resource<List<Match>>>
     fun getStandingByLeague(competitionId: String, matchday: Int): Flow<Resource<StandingData>>
     fun getLeagueInfo(competitionId: String): Flow<Resource<CompetitionLeague>>
}