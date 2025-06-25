package com.jerry.ronaldo.siufascore.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jerry.ronaldo.siufascore.data.model.CommentThread
import com.jerry.ronaldo.siufascore.data.remote.YoutubeApiService
import timber.log.Timber

class YoutubeCmtsPagingSource(
    private val apiService: YoutubeApiService,
    private val videoId: String,
    private val order: String = "relevance"
) : PagingSource<String, CommentThread>() {
    override fun getRefreshKey(state: PagingState<String, CommentThread>): String? {
        return null
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, CommentThread> {
        return try {
            val pageToken = params.key
            val response = apiService.getCommentThreads(
                videoId = videoId,
                order = order,
                maxResults = params.loadSize,
                pageToken = pageToken
            )
            val comments = response.items
            LoadResult.Page(
                data = comments,
                prevKey = null,
                nextKey = response.nextPageToken
            )
        } catch (e: Exception) {
            Timber.tag("CommentsPaging").e(e, "lỗi tải comments")
            LoadResult.Error(e)
        }
    }

}