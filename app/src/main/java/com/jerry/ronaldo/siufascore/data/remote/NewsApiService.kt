package com.jerry.ronaldo.siufascore.data.remote

import com.jerry.ronaldo.siufascore.data.model.ArticleDetailsRequest
import com.jerry.ronaldo.siufascore.data.model.NewsDetailResponse
import com.jerry.ronaldo.siufascore.data.model.NewsRequest
import com.jerry.ronaldo.siufascore.data.model.NewsResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface NewsApiService {
    @POST("api/v1/article/getArticles")
    suspend fun searchArticles(
        @Body request: NewsRequest
    ): NewsResponse

    @POST("api/v1/article/getArticle")
    suspend fun getDetailNews(
        @Body request: ArticleDetailsRequest
    ): Map<String,NewsDetailResponse>//xử lý key động sử dụng map
}

