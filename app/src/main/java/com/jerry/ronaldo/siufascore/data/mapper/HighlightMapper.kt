package com.jerry.ronaldo.siufascore.data.mapper

import com.jerry.ronaldo.siufascore.data.model.DetailVideoItem
import com.jerry.ronaldo.siufascore.data.model.PlaylistItem
import com.jerry.ronaldo.siufascore.domain.model.VideoItem
import com.jerry.ronaldo.siufascore.utils.formatViewCount
import com.jerry.ronaldo.siufascore.utils.formatYouTubeTime

fun PlaylistItem.toDomain(): VideoItem {
    return VideoItem(
        videoId = this.snippet.resourceId.videoId,
        title = this.snippet.title,
        description = this.snippet.description,
        thumbnailUrl = this.snippet.thumbnails.high?.url ?: "",
        publishedAt = this.snippet.publishedAt.formatYouTubeTime(),
    )
}
fun DetailVideoItem.toDomain(): VideoItem {
    return VideoItem(
        videoId = this.videoId,
        title = this.snippet.title,
        description = this.snippet.description,
        thumbnailUrl = this.snippet.thumbnails.high?.url ?: "",
        publishedAt = this.snippet.publishedAt.formatYouTubeTime(),
        viewCount = this.statistics.viewCount.formatViewCount(),
        likeCount = this.statistics.likeCount,
        commentCount = this.statistics.commentCount
    )
}