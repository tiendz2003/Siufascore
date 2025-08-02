package com.jerry.ronaldo.siufascore.data.remote

import com.jerry.ronaldo.siufascore.data.model.CurrentRoundResponse
import com.jerry.ronaldo.siufascore.data.model.DetailMatchResponse
import com.jerry.ronaldo.siufascore.data.model.MatchResponse
import com.jerry.ronaldo.siufascore.data.model.PlayerDetailResponse
import com.jerry.ronaldo.siufascore.data.model.PlayerTeamsResponse
import com.jerry.ronaldo.siufascore.data.model.PlayerTrophiesResponse
import com.jerry.ronaldo.siufascore.data.model.PlayersSearchResponse
import com.jerry.ronaldo.siufascore.data.model.StandingsResponse
import com.jerry.ronaldo.siufascore.data.model.TeamDetailResponse
import com.jerry.ronaldo.siufascore.data.model.TeamsSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FootballApiService {
    // Lấy lịch thi đấu theo vòng đấu hiện tại
    @GET("fixtures")
    suspend fun getMatchByLeague(
        @Query("league") leagueId: Int,
        @Query("season") season: Int = 2024,
        @Query("round") round: String
    ): MatchResponse

    //Lấy chi tiết thông tin 1 trận đấu
    @GET("fixtures")
    suspend fun getDetailMatch(@Query("id") matchId: Int): DetailMatchResponse

    // Lấy fixtures đang diễn ra (live)
    @GET("fixtures")
    suspend fun getLiveFixtures(
        @Query("live") live: String = "all",
        @Query("league") leagueId: String? = null
    ): MatchResponse

    // Lấy fixtures theo đội
    @GET("fixtures")
    suspend fun getFixturesByTeam(
        @Query("team") teamId: Int,
        @Query("season") season: Int = 2024,
        @Query("last") last: Int? = null // Last N fixtures
    ): MatchResponse

    @GET("fixtures/headtohead")
    suspend fun getHeadToHead(
        @Query("h2h") teams: String, // Format: "teamId1-teamId2"
        @Query("season") season: Int = 2024,
        @Query("last") last: Int = 5 // Last N fixtures
    ): MatchResponse

    //Lấy bảng xếp hạng theo giải đấu
    @GET("standings")
    suspend fun getStandingByLeague(
        @Query("league") leagueId: Int,
        @Query("season") season: Int
    ): StandingsResponse

    //Lấy thông tin vòng đấu hiện tại
    @GET("fixtures/rounds")
    suspend fun getCurrentSeasonInfo(
        @Query("league") competitionId: Int,
        @Query("season") season: Int = 2025,
        @Query("current") current: Boolean = true
    ): CurrentRoundResponse

    // tìm kiếm câu lạc bộ
    @GET("teams")
    suspend fun searchTeams(
        @Query("search") search: String,
    ): TeamsSearchResponse

    //tìm kiếm cầu thủ
    @GET("players/profiles")
    suspend fun searchPlayers(
        @Query("search") search: String,
        @Query("page") page: Int = 1
    ): PlayersSearchResponse

    @GET("players/teams")
    suspend fun getPlayerTeams(
        @Query("player") playerId: Int,
    ): PlayerTeamsResponse

    @GET("trophies")
    suspend fun getPlayerTrophies(
        @Query("player") playerId: Int,
    ): PlayerTrophiesResponse

    //chi tiết cầu thủ
    @GET("players")
    suspend fun getDetailPlayerInfo(
        @Query("id") playerId: Int,
        @Query("season") season: Int
    ): PlayerDetailResponse

    @GET("teams/statistics")
    suspend fun getTeamStatistics(
        @Query("league") leagueId: Int,
        @Query("team") teamId: Int,
        @Query("season") season: Int
    ): TeamDetailResponse


}