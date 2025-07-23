package com.jerry.ronaldo.siufascore.domain.repository

import com.jerry.ronaldo.siufascore.data.model.StreamResponse
import com.jerry.ronaldo.siufascore.utils.Resource

interface LiveStreamRepository {
   // suspend fun getLiveStreams(): Resource<List<LiveMatch>>
    suspend fun getLiveStreamById(id: Long): Resource<StreamResponse>
}