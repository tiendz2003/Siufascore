package com.jerry.ronaldo.siufascore.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jerry.ronaldo.siufascore.data.mapper.toDomain
import com.jerry.ronaldo.siufascore.data.model.ConditionDto
import com.jerry.ronaldo.siufascore.data.model.FilterDto
import com.jerry.ronaldo.siufascore.data.model.NewsRequest
import com.jerry.ronaldo.siufascore.data.model.QueryDto
import com.jerry.ronaldo.siufascore.data.model.QueryFilterDto
import com.jerry.ronaldo.siufascore.data.remote.NewsApiService
import com.jerry.ronaldo.siufascore.domain.model.Article
import kotlinx.serialization.SerializationException
import timber.log.Timber

class NewsPagingSource(
    private val newsApiService: NewsApiService,
    private val apiKey: String,
    private val conceptUri: String,
) : PagingSource<Int, Article>() {
    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val page = params.key ?: 1
        return try {
            val response = newsApiService.searchArticles(
                request = NewsRequest(
                    query = QueryDto(
                        queryFilter = QueryFilterDto(
                            and = buildList {
                                add(
                                    ConditionDto(
                                        conceptUri = conceptUri,
                                        lang = "vie"
                                    )
                                )
                            }
                        ),
                        filter = FilterDto()
                    ),
                    resultType = "articles",
                    articlesSortBy = "date",
                    includeArticleLocation = true,
                    includeConceptDescription = true,
                    apiKey = apiKey,
                    articlesPage = page,
                    articlesCount = params.loadSize
                )
            )
            val articles = response.articles.results.map {
                it.toDomain()
            }
            Timber.tag("NewsPagingSource").d("articles: $articles")
            LoadResult.Page(
                data = articles,
                prevKey = if (page == 1) null else page - 1,
                nextKey = if (articles.isEmpty() && page < (response.articles.totalResults / response.articles.count + 1)) page + 1 else null
            )
        } catch (e: SerializationException) {
            Timber.tag("NewsPagingSource").d("Error: ${e.message}")
            LoadResult.Error(e)
        } catch (e: Exception) {
            Timber.tag("NewsPagingSource").d("Error: ${e.message}")
            LoadResult.Error(e)
        }
    }

}