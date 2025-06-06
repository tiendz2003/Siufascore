package com.jerry.ronaldo.siufascore.data.remote

import com.jerry.ronaldo.siufascore.data.model.LeagueResponse
import com.jerry.ronaldo.siufascore.data.model.MatchesResponse
import com.jerry.ronaldo.siufascore.data.model.StandingListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FootBallApiService {
    @GET("/v4/competitions/{competitionId}/matches")
    suspend fun getMatchByLeague(
        @Path("competitionId") competitionId: String,
        @Query("matchday") matchDay: Int
    ): MatchesResponse

    @GET("/v4/competitions/{id}/standings")
    suspend fun getStandingByLeague(
        @Path("id") id: String,
        @Query("matchday") matchDay: Int
    ): StandingListResponse

    @GET("/v4/competitions/{id}")
    suspend fun getCurrentSeasonInfo(
        @Path("id") competitionId: String,
    ): LeagueResponse
}