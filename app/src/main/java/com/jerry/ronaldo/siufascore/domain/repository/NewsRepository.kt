package com.jerry.ronaldo.siufascore.domain.repository

import androidx.paging.PagingData
import com.jerry.ronaldo.siufascore.domain.model.Article
import com.jerry.ronaldo.siufascore.utils.Resource
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getNews(
        conceptUri: String,
    ): Flow<PagingData<Article>>

    suspend fun getDetailNews(newsUri: String): Resource<Article>
}