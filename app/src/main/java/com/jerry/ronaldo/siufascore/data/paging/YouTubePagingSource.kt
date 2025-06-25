package com.jerry.ronaldo.siufascore.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jerry.ronaldo.siufascore.data.mapper.toDomain
import com.jerry.ronaldo.siufascore.data.remote.YoutubeApiService
import com.jerry.ronaldo.siufascore.domain.model.VideoItem
import javax.inject.Inject

class YouTubePagingSource @Inject constructor(
    private val youtubeApiService: YoutubeApiService,
    private val playlistId: String
) : PagingSource<String, VideoItem>() {
    override fun getRefreshKey(state: PagingState<String, VideoItem>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage =
                state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, VideoItem> {
        return try {
            val pageToken = params.key
            val response = youtubeApiService.getPlaylistItems(
                playlistId = playlistId,
                maxResults = params.loadSize,
                pageToken = pageToken
            )
            val videoItemsData = response.items.map {
                it.toDomain()
            }
            LoadResult.Page(
                data = videoItemsData,
                prevKey = response.prevPageToken,
                nextKey = response.nextPageToken
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}