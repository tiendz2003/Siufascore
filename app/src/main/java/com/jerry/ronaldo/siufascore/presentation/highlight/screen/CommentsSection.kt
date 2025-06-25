package com.jerry.ronaldo.siufascore.presentation.highlight.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.data.model.Comment
import com.jerry.ronaldo.siufascore.data.model.CommentThread
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.utils.formatYouTubeTime

@Composable
fun CommentsContent(
    comments: LazyPagingItems<CommentThread>?,
    videoId: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var commentText by remember { mutableStateOf("") }
    // Box chứa comments và input comment bar
    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (comments != null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize().weight(1f),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 80.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(count = comments.itemCount){index->
                        val comment = comments[index]
                        comment?.let {
                            CommentThreadItem(
                                commentThread = it,
                                isSignedIn = true
                            )
                        }
                    }
                }
            }
        }

        // Input comment bar - fixed ở đáy
        Surface (
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 4.dp
        ){
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    imageVector = ImageVector.vectorResource(id = R.drawable.premier_league),
                    contentDescription = "UserImage",
                )
                Spacer(Modifier.width(12.dp))
                TextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Thêm bình luận") },
                    shape = RoundedCornerShape(20.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    )
                )
                AnimatedVisibility(
                    visible = commentText.isNotBlank()
                ) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Gửi bình luận",
                            tint = Purple
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentThreadItem(
    commentThread: CommentThread,
    isSignedIn: Boolean,
    modifier: Modifier = Modifier
) {
    val topComment = commentThread.snippet.topLevelComment
    var showReplies by remember { mutableStateOf(false) }
    var showReplyInput by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Top-level comment
        CommentItem(
            comment = topComment,
            onReplyClick = { showReplyInput = !showReplyInput },
            isSignedIn = isSignedIn
        )

        // Show replies button
        if (commentThread.snippet.totalReplyCount > 0) {
            TextButton(
                onClick = { showReplies = !showReplies },
                modifier = Modifier.padding(start = 56.dp)
            ) {
                Text(
                    text = if (showReplies) {
                        "Ẩn bình luận"
                    } else {
                        "Xem ${commentThread.snippet.totalReplyCount} trả lời"
                    }
                )
            }

            // Replies list
            if (showReplies && commentThread.replies != null) {
                Column(
                    modifier = Modifier.padding(start = 56.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    commentThread.replies.comments.forEach { reply ->
                        CommentItem(
                            comment = reply,
                            onReplyClick = null, // No nested replies
                            isSignedIn = isSignedIn,
                            isReply = true
                        )
                    }
                }
            }
        }
    }
}
@Composable
fun CommentItem(
    modifier: Modifier = Modifier,
    comment: Comment,
    onReplyClick: (() -> Unit)? = null,
    isSignedIn: Boolean,
    isReply: Boolean = false,

) {
    val snippet = comment.snippet

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // User avatar
        AsyncImage(
            model = snippet.authorProfileImageUrl,
            contentDescription = "User Avatar",
            modifier = Modifier
                .size(if (isReply) 32.dp else 40.dp)
                .clip(CircleShape),
            placeholder = painterResource(R.drawable.premier_league),
            error = painterResource(R.drawable.premier_league)
        )

        Column(modifier = Modifier.weight(1f)) {
            // Author and time
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = snippet.authorDisplayName,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )
                )

                Text(
                    text = snippet.publishedAt.formatYouTubeTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Comment text
            Text(
                text = snippet.textDisplay,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
              /*  // Like button
                CommentActionButton(
                    icon = Icons.Default.ThumbUp,
                    count = snippet.likeCount,
                    isSelected = snippet.viewerRating == "like",
                    onClick = {
                        if (isSignedIn) {
                            val rating = if (snippet.viewerRating == "like") {
                                CommentRating.NONE
                            } else {
                                CommentRating.LIKE
                            }
                            onRateComment(comment.id, rating)
                        }
                    },
                    enabled = isSignedIn
                )*/
                // Reply button (only for top-level comments)
                if (onReplyClick != null) {
                    TextButton(
                        onClick = onReplyClick,
                        enabled = isSignedIn
                    ) {
                        Text("Reply")
                    }
                }
            }
        }
    }
}