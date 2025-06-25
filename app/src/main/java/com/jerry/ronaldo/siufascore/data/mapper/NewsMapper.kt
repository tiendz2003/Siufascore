package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.ArticleDto
import com.jerry.ronaldo.siufascore.data.model.NewsInfo
import com.jerry.ronaldo.siufascore.domain.model.Article
import com.jerry.ronaldo.siufascore.domain.model.Author
import com.jerry.ronaldo.siufascore.domain.model.Source
import com.jerry.ronaldo.siufascore.utils.extractDescription
import com.jerry.ronaldo.siufascore.utils.formatYouTubeTime


fun ArticleDto.toDomain(): Article {
    return Article(
        uri = this.uri,
        title = this.title,
        description = this.body.extractDescription(),
        body = this.body,
        url = this.url,
        source = Source(
            uri = this.source.uri,
            title = this.source.title,
            description = null
        ),
        publishedAt = this.dateTime.formatYouTubeTime(),
        imageUrl = this.image,
        authors = this.authors?.map {
            Author(name = it.name, uri = it.uri)
        } ?: emptyList(),
        language = this.lang,
        sentiment = this.sentiment,
    )
}

fun NewsInfo.toDomain(): Article {
    return Article(
        uri = this.uri,
        title = this.title,
        description = this.body.extractDescription(),
        body = this.body,
        url = this.url,
        source = Source(
            uri = this.source.uri,
            title = this.source.title,
            description = null
        ),
        publishedAt = this.dateTime.formatYouTubeTime(),
        imageUrl = this.image,
        authors = this.authors.map {
            Author(name = it.name, uri = it.uri)
        } ?: emptyList(),
        language = this.lang,
        sentiment = this.sentiment?.toDouble(),
    )
}

    