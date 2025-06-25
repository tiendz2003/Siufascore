package com.jerry.ronaldo.siufascore.domain.model

data class VideoItem(
    val videoId: String,
    val title: String,
    val description: String,
    val thumbnailUrl: String,
    val publishedAt: String,
    val viewCount: String?= null,
    val likeCount: String?=null,
    val commentCount: String?=null
)
