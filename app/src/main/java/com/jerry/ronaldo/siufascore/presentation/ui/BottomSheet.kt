package com.jerry.ronaldo.siufascore.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.jerry.ronaldo.siufascore.data.model.CommentThread
import com.jerry.ronaldo.siufascore.presentation.highlight.screen.CommentsContent
import com.jerry.ronaldo.siufascore.presentation.highlight.screen.CommentsHeader
import com.jerry.ronaldo.siufascore.presentation.highlight.screen.DragHandle

@Composable
fun BottomSheet(
    comments: LazyPagingItems<CommentThread>?,
    videoId: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(
            topStart = 16.dp,
            topEnd = 16.dp
        ),
        color = Color.White,
        shadowElevation = 8.dp,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            DragHandle(
                onDismiss = onDismiss
            )
            CommentsHeader(
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            CommentsContent(
                comments = comments,
                videoId = videoId,
                onDismiss = onDismiss,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}