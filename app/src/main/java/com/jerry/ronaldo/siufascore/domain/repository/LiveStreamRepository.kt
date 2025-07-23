package com.jerry.ronaldo.siufascore.domain.repository

import com.jerry.ronaldo.siufascore.data.model.CommentResult
import com.jerry.ronaldo.siufascore.data.model.LiveComment
import com.jerry.ronaldo.siufascore.data.model.StreamResponse
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow

interface LiveStreamRepository {
    suspend fun getLiveStreamById(id: Long): Resource<StreamResponse>
    fun getComments(matchId: String, limit: Int = 50): Flow<Resource<List<LiveComment>>>
    fun getCommentsWithPagination(
        matchId: String,
        limit: Int = 20,
        startAfterKey: String? = null
    ): Flow<Resource<CommentResult>>
    suspend fun postComment(matchId: String, comment: LiveComment): Resource<String>
}