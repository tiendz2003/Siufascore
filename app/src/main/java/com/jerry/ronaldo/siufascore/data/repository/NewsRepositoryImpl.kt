package com.jerry.ronaldo.siufascore.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jerry.ronaldo.siufascore.data.mapper.toDomain
import com.jerry.ronaldo.siufascore.data.model.ArticleDetailsRequest
import com.jerry.ronaldo.siufascore.data.paging.NewsPagingSource
import com.jerry.ronaldo.siufascore.data.remote.NewsApiService
import com.jerry.ronaldo.siufascore.domain.model.Article
import com.jerry.ronaldo.siufascore.domain.repository.NewsRepository
import com.jerry.ronaldo.siufascore.utils.IODispatcher
import com.jerry.ronaldo.siufascore.utils.NewsClientId
import com.jerry.ronaldo.siufascore.utils.Resource
import com.jerry.ronaldo.siufascore.utils.handleException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import javax.inject.Inject

class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    @NewsClientId val clientId: String,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher
) : NewsRepository {
    override fun getNews(
        conceptUri: String,
    ): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = true,
                prefetchDistance = 5,
                initialLoadSize = 20
            ),
            pagingSourceFactory = {
                NewsPagingSource(
                    newsApiService = newsApiService,
                    apiKey = clientId,
                    conceptUri = conceptUri,
                )
            }
        ).flow.flowOn(ioDispatcher)

    }

    override suspend fun getDetailNews(newsUri: String): Resource<Article> =
        try {
            val response = newsApiService.getDetailNews(
                request = ArticleDetailsRequest(
                    articleUri = listOf(newsUri),
                    apiKey = clientId
                )
            )
            val detailArticle =
                response[newsUri]?.info?.toDomain()//Lấy info bài viết dựa theo key
            if (detailArticle != null) {
                Resource.Success(detailArticle)
            } else {
                Resource.Error(Exception("\"Không tìm thấy bài báo\""))
            }
        } catch (e: Exception) {
            Timber.tag("NewsRepositoryImpl").e("Lỗi:${e.message}")
            Resource.Error(e.handleException())
        }
}