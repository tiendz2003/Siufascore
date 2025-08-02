package com.jerry.ronaldo.siufascore.data.remote

import com.jerry.ronaldo.siufascore.data.model.StreamApiResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface LiveStreamingApiService {

    @GET("api/live/fixture/{matchId}/stream")
    suspend fun getStreamUrl(
        @Path("matchId") matchId: Long,
        @Header("X-Device-Type") deviceType: String = "mobile",
        @Header("User-Agent") userAgent: String = "LiveStreamApp/1.0 (Android)"
    ): StreamApiResponse

    /*@GET("api/live/matches/today")
    suspend fun getTodayMatches(
        @Query("onlyWithStream") onlyWithStream: Boolean = false
    ): LiveStreamResponse<List<LiveMatch>>*/

    /*@GET("live/match/{matchId}/check")
    suspend fun checkStreamAvailability(
        @Path("matchId") matchId: Long
    ): LiveStreamResponse<StreamStatusDto>

    @GET("live/health")
    suspend fun healthCheck(): LiveStreamResponse<HealthDto>*/
}