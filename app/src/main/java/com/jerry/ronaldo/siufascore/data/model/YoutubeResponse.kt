package com.jerry.ronaldo.siufascore.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class YoutubePlaylistResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    val pageInfo: PageInfo,
    val items: List<PlaylistItem>
)

@Serializable
data class YoutubeSearchResponse(
    val kind: String,
    val etag: String,
    val nextPageToken: String? = null,
    val prevPageToken: String? = null,
    val pageInfo: PageInfo,
    val items: List<SearchResultItem>
)

@Serializable
data class PlaylistItem(
    val kind: String,
    val etag: String,
    val id: String,
    val snippet: PlaylistItemSnippet
)

@Serializable
data class SearchResultItem(
    val kind: String,
    val etag: String,
    val id: VideoId,
    val snippet: VideoSnippet
)

@Serializable
data class VideoId(
    val kind: String,
    val videoId: String
)

@Serializable
data class PlaylistItemSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String,
    val playlistId: String,
    val position: Int,
    val resourceId: ResourceId
)

@Serializable
data class VideoSnippet(
    val publishedAt: String,
    val channelId: String,
    val title: String,
    val description: String,
    val thumbnails: Thumbnails,
    val channelTitle: String
)

@Serializable
data class DetailVideoItem(
    @SerialName("id") // ✅ Map "id" field từ JSON to "videoId" property
    val videoId: String,
    val snippet: VideoSnippet,
    val statistics: VideoStatistics,
)

@Serializable
data class VideoStatistics(
    @SerialName("viewCount")
    val viewCount: String,
    @SerialName("likeCount")
    val likeCount: String,
    @SerialName("commentCount")
    val commentCount: String
)

@Serializable
data class DetailVideoResponse(
    val items: List<DetailVideoItem>
)

@Serializable
data class ResourceId(
    val kind: String,
    val videoId: String
)

@Serializable
data class Thumbnails(
    val default: Thumbnail? = null,
    val medium: Thumbnail? = null,
    val high: Thumbnail? = null,
    val standard: Thumbnail? = null,
    val maxres: Thumbnail? = null
)

@Serializable
data class Thumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

@Serializable
data class PageInfo(
    @SerialName("totalResults")
    val totalResults: Int,
    @SerialName("resultsPerPage")
    val resultsPerPage: Int
)

//Comment Response
@Serializable
data class CommentThreadResponse(
    @SerialName("kind") val kind: String = "youtube#commentThreadListResponse",
    @SerialName("etag") val etag: String = "",
    @SerialName("nextPageToken") val nextPageToken: String? = null,
    @SerialName("pageInfo") val pageInfo: PageInfo,
    @SerialName("items") val items: List<CommentThread>
)

@Serializable
data class CommentThread(
    @SerialName("kind") val kind: String = "youtube#commentThread",
    @SerialName("etag") val etag: String = "",
    @SerialName("id") val id: String,
    @SerialName("snippet") val snippet: CommentThreadSnippet,
    @SerialName("replies") val replies: CommentReplies? = null
)

@Serializable
data class CommentThreadSnippet(
    @SerialName("channelId") val channelId: String = "",
    @SerialName("videoId") val videoId: String,
    @SerialName("topLevelComment") val topLevelComment: Comment,
    @SerialName("canReply") val canReply: Boolean = true,
    @SerialName("totalReplyCount") val totalReplyCount: Int = 0,
    @SerialName("isPublic") val isPublic: Boolean = true
)

@Serializable
data class Comment(
    @SerialName("kind") val kind: String = "youtube#comment",
    @SerialName("etag") val etag: String = "",
    @SerialName("id") val id: String,
    @SerialName("snippet") val snippet: CommentSnippet
)

@Serializable
data class CommentSnippet(
    @SerialName("authorDisplayName") val authorDisplayName: String,
    @SerialName("authorProfileImageUrl") val authorProfileImageUrl: String,
    @SerialName("authorChannelUrl") val authorChannelUrl: String = "",
    @SerialName("authorChannelId") val authorChannelId: CommentAuthorChannelId? = null,
    @SerialName("videoId") val videoId: String = "",
    @SerialName("textDisplay") val textDisplay: String,
    @SerialName("textOriginal") val textOriginal: String,
    @SerialName("publishedAt") val publishedAt: String,
    @SerialName("updatedAt") val updatedAt: String,
    @SerialName("likeCount") val likeCount: Int = 0,
    @SerialName("moderationStatus") val moderationStatus: String = "", // e.g. "published", "heldForReview", "likelySpam"
    @SerialName("parentId") val parentId: String? = null,
    @SerialName("canRate") val canRate: Boolean = true,
    @SerialName("viewerRating") val viewerRating: String = "none" // "none", "like", "dislike"
)

@Serializable
data class CommentAuthorChannelId(
    @SerialName("value") val value: String
)

@Serializable
data class CommentReplies(
    @SerialName("comments") val comments: List<Comment>
)
