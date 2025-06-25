package com.jerry.ronaldo.siufascore.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jerry.ronaldo.siufascore.data.mapper.toDomain
import com.jerry.ronaldo.siufascore.data.model.CommentThread
import com.jerry.ronaldo.siufascore.data.paging.YouTubePagingSource
import com.jerry.ronaldo.siufascore.data.paging.YoutubeCmtsPagingSource
import com.jerry.ronaldo.siufascore.data.remote.YoutubeApiService
import com.jerry.ronaldo.siufascore.domain.model.VideoItem
import com.jerry.ronaldo.siufascore.domain.repository.HighlightRepository
import com.jerry.ronaldo.siufascore.utils.IODispatcher
import com.jerry.ronaldo.siufascore.utils.LeagueData
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.handleException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class HighlightRepositoryImpl @Inject constructor(
    private val youtubeApiService: YoutubeApiService,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : HighlightRepository {
    companion object {
        const val PREMIER_LEAGUE_PLAYLIST = "PLiaWrX4zmrTmVTbQGnQv2FQQ3h09Kk5_K"
        const val SERIE_A_PLAYLIST = "PLFTjYT0jsEKyiD-O4v7jPgjbcM43JCPbW"
        const val LALIGA_PLAYLIST = "PLZSqlXtjILv0bpkjFKaryelOhxIeyYSq_"
        const val CL_PLAYLIST = "PL0Z61uaqGuUaL9O8I1cYj7NdDTSE6KXLf"
    }

    override fun getPlayListVideo(query: String): Flow<PagingData<VideoItem>> {
        val leagueId = LeagueData.getPlaylistById(query)
        return Pager(
            config = PagingConfig(
                pageSize = 25,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = 25,
            ),
            pagingSourceFactory = {
                YouTubePagingSource(youtubeApiService, leagueId)
            }
        ).flow.flowOn(ioDispatcher).catch {
            Timber.tag("HighlightRepositoryImpl").e("getPlayListVideo: ${it.message}")
        }
    }

    override fun findVideoInList(query: String): Flow<Resource<List<VideoItem>>> {
        return flow {
            try {
                var nextPageToken: String? = null
                val results = mutableListOf<VideoItem>()
                var pageCount = 0
                do {
                    if (pageCount >= 5) break
                    val response = youtubeApiService.getPlaylistItems(
                        playlistId = PREMIER_LEAGUE_PLAYLIST,
                        pageToken = nextPageToken
                    )
                    val videos = response.items
                    val videosFilter = videos.filter { video ->
                        video.snippet.title.contains(query, ignoreCase = true)
                    }.map {
                        it.toDomain()
                    }
                    results.addAll(videosFilter)
                    nextPageToken = response.nextPageToken
                    pageCount++
                } while (nextPageToken != null && results.size < 10)
                emit(Resource.Success(results))
            } catch (e: Exception) {
                emit(Resource.Error(e.handleException()))
            }
        }.flowOn(ioDispatcher)
    }

    override fun getVideoDetailsInfo(videoId: String): Flow<Resource<VideoItem>> {
        return flow {
            try {
                val response =
                    youtubeApiService.getVideoDetailsInfo(videoId = videoId).items.firstOrNull()
                Timber.tag("HighlightRepositoryImpl").d("getVideoDetailsInfo: $response")
                val infoVideo = response?.toDomain()
                Timber.tag("HighlightRepositoryImpl").d("getVideoDetailsInfo: $infoVideo")
                emit(Resource.Success(infoVideo!!))
            } catch (e: Exception) {
                Timber.tag("HighlightRepositoryImpl").e("getVideoDetailsInfo: ${e.message}")
                emit(Resource.Error(e.handleException()))
            }
        }.flowOn(ioDispatcher)
    }

    override fun getVideoComments(
        videoId: String,
    ): Flow<PagingData<CommentThread>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                prefetchDistance = 5,
                enablePlaceholders = false,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                YoutubeCmtsPagingSource(
                    apiService = youtubeApiService,
                    videoId = videoId,
                )
            }
        ).flow.flowOn(ioDispatcher).catch {
            Timber.tag("HighlightRepositoryImpl").e("getComment: ${it.message}")
        }
    }

}