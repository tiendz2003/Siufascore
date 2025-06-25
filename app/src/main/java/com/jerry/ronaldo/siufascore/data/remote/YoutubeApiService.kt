package com.jerry.ronaldo.siufascore.data.remote

import com.jerry.ronaldo.siufascore.data.model.CommentThreadResponse
import com.jerry.ronaldo.siufascore.data.model.DetailVideoResponse
import com.jerry.ronaldo.siufascore.data.model.YoutubePlaylistResponse
import com.jerry.ronaldo.siufascore.data.model.YoutubeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeApiService {
    @GET("playlistItems")
    suspend fun getPlaylistItems(
        @Query("part") part: String = "snippet",
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int = 50,
        @Query("pageToken") pageToken: String? = null
    ): YoutubePlaylistResponse

    @GET("videos")
    suspend fun getVideoDetailsInfo(
        @Query("part") part: String = "snippet,statistics,contentDetails",
        @Query("id") videoId: String,
    ): DetailVideoResponse

    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,
        @Query("type") type: String = "video",
        @Query("maxResults") maxResults: Int = 10,
        @Query("order") order: String = "relevance"
    ): YoutubeSearchResponse

    @GET("commentThreads")
    suspend fun getCommentThreads(
        @Query("part") part: String = "snippet,replies",
        @Query("videoId") videoId: String,
        @Query("order") order: String = "relevance",
        @Query("maxResults") maxResults: Int = 20,
        @Query("pageToken") pageToken: String? = null,
    ): CommentThreadResponse
}
