package com.jerry.ronaldo.siufascore.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NewsRequest(
    @SerialName("query") val query: QueryDto,
    @SerialName("resultType") val resultType: String = "articles",
    @SerialName("articlesSortBy") val articlesSortBy: String = "date",
    @SerialName("includeArticleLocation") val includeArticleLocation: Boolean = true,
    @SerialName("includeConceptDescription") val includeConceptDescription: Boolean = true,
    @SerialName("apiKey") val apiKey: String,
    @SerialName("articlesPage") val articlesPage: Int = 1,
    @SerialName("articlesCount") val articlesCount: Int = 20
)

@Serializable
data class QueryDto(
    @SerialName("\$query") val queryFilter: QueryFilterDto,
    @SerialName("\$filter") val filter: FilterDto? = null
)

@Serializable
data class QueryFilterDto(
    @SerialName("\$and") val and: List<ConditionDto>
)

@Serializable
data class ConditionDto(
    @SerialName("conceptUri") val conceptUri: String? = null,
    @SerialName("lang") val lang: String? = null,
)

@Serializable
data class FilterDto(
    @SerialName("forceMaxDataTimeWindow") val forceMaxDataTimeWindow: String = "31"
)

@Serializable
data class NewsResponse(
    @SerialName("articles") val articles: ArticlesWrapperDto
)

@Serializable
data class ArticlesWrapperDto(
    @SerialName("results") val results: List<ArticleDto>,
    @SerialName("totalResults") val totalResults: Int,
    @SerialName("page") val page: Int,
    @SerialName("count") val count: Int
)

@Serializable
data class ArticleDto(
    @SerialName("uri") val uri: String,
    @SerialName("title") val title: String,
    @SerialName("body") val body: String?,
    @SerialName("url") val url: String,
    @SerialName("source") val source: SourceDto,
    @SerialName("dateTime") val dateTime: String,
    @SerialName("image") val image: String?,
    @SerialName("authors") val authors: List<AuthorDto>?,
    @SerialName("lang") val lang: String,
    @SerialName("sentiment") val sentiment: Double?,
)

@Serializable
data class ArticleDetailsDto(
    @SerialName("info") val info: ArticleDto,
    @SerialName("source") val source: SourceDto,
    @SerialName("authors") val authors: List<String>,
    @SerialName("image") val image: String? = null,
    @SerialName("eventUri") val eventUri: String? = null,
    @SerialName("sentiment") val sentiment: Float? = null
)

@Serializable
data class SourceDto(
    @SerialName("uri") val uri: String,
    @SerialName("dataType") val dataType: String,
    @SerialName("title") val title: String
)

@Serializable
data class AuthorDto(
    @SerialName("name") val name: String,
    @SerialName("uri") val uri: String?
)


@Serializable
data class ArticleDetailsRequest(
    @SerialName("articleUri") val articleUri: List<String>,
    @SerialName("apiKey") val apiKey: String
)


@Serializable
data class NewsDetailResponse(
    val info: NewsInfo
)

@Serializable
data class NewsInfo(
    val uri: String,
    val lang: String,
    val isDuplicate: Boolean,
    val date: String,
    val time: String,
    val dateTime: String,
    val dateTimePub: String,
    val dataType: String,
    val url: String,
    val title: String,
    val body: String,
    val source: SourceDto,
    val authors: List<AuthorDto> = emptyList(),
    val image: String? = null,
    val eventUri: String? = null,
    val sentiment: String? = null
){}