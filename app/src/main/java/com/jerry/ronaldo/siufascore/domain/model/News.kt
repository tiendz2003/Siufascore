package com.jerry.ronaldo.siufascore.domain.model

data class Article(
    val uri: String,
    val title: String,
    val description: String?,
    val body: String?,
    val url: String,
    val source: Source,
    val publishedAt: String,
    val imageUrl: String?,
    val authors: List<Author>,
    val language: String,
    val sentiment: Double?,
)

data class Source(
    val uri: String,
    val title: String,
    val description: String?
)

data class Author(
    val name: String,
    val uri: String?
)


data class NewsQuery(
    val conceptUri: String? = null,
    val keywords: List<String>? = null,
    val language: String = "vie",
    val sortBy: String = "date",
    val maxDataTimeWindow: Int = 31,
    val sources: List<String>? = null,
    val categories: List<String>? = null
)