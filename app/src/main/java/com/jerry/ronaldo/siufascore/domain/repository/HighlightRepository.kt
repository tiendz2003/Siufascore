package com.jerry.ronaldo.siufascore.domain.repository

import androidx.annotation.WorkerThread
import androidx.paging.PagingData
import com.jerry.ronaldo.siufascore.data.model.CommentThread
import com.jerry.ronaldo.siufascore.domain.model.VideoItem
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow

interface HighlightRepository {
    @WorkerThread
    fun getPlayListVideo(query: String): Flow<PagingData<VideoItem>>
    @WorkerThread
    fun findVideoInList(query: String): Flow<Resource<List<VideoItem>>>
    @WorkerThread
    fun getVideoDetailsInfo(videoId: String): Flow<Resource<VideoItem>>
    @WorkerThread
    fun getVideoComments(
        videoId: String,
    ): Flow<PagingData<CommentThread>>
}