package com.jerry.ronaldo.siufascore.domain.repository

import androidx.annotation.WorkerThread
import androidx.paging.PagingData
import com.jerry.ronaldo.siufascore.domain.model.Article
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    @WorkerThread
    fun getNews(
        conceptUri: String,
    ): Flow<PagingData<Article>>

    @WorkerThread
    suspend fun getDetailNews(newsUri: String): Resource<Article>
}