package com.jerry.ronaldo.siufascore.presentation.highlight.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.presentation.highlight.DetailHighLightIntent
import com.jerry.ronaldo.siufascore.presentation.highlight.DetailHighLightUiState
import com.jerry.ronaldo.siufascore.presentation.highlight.DetailHighlightViewModel
import com.jerry.ronaldo.siufascore.presentation.highlight.HighlightViewModel
import com.jerry.ronaldo.siufascore.presentation.ui.BottomSheet
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HighLightDetailScreen(
    viewModel: DetailHighlightViewModel = hiltViewModel(),
    listViewModel: HighlightViewModel = hiltViewModel(),
) {
    val videoAspectRatio = 16f / 9f
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Timber.tag("HighLightDetailScreen").d("HighLightDetailScreen: ${uiState.infoVideo}")
    val isCommentsExpanded by viewModel.isCommentsExpanded.collectAsState()
    val comments = if (isCommentsExpanded) {
        viewModel.videoComments.collectAsLazyPagingItems()
    } else null
    val modalBottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )
    LaunchedEffect(viewModel.videoId) {
            viewModel.sendIntent(DetailHighLightIntent.SetVideoId(viewModel.videoId))
    }
    LaunchedEffect(isCommentsExpanded) {
        if (isCommentsExpanded) {
            modalBottomSheetState.show()
        } else {
            if (modalBottomSheetState.isVisible) {
                modalBottomSheetState.hide()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            YoutubePlayerScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(videoAspectRatio),
                videoId = uiState.videoId ?: viewModel.videoId,
                onCurrentSecondChanged = { second ->

                }
            )
            Spacer(Modifier.height(8.dp))
            VideoInfoSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                title = uiState.infoVideo?.title ?: "Không rõ tiêu đề",
                viewCount = uiState.infoVideo?.viewCount ?: "Không rõ lượt xem",
                timeAgo = uiState.infoVideo?.publishedAt ?: "Không rõ ngày đăng"
            )
            Spacer(Modifier.height(8.dp))
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .padding(horizontal = 16.dp)
            )
            CommentSection(
                modifier = Modifier.padding(16.dp),
                uiState = uiState,
                onOpenComment = {
                    viewModel.sendIntent(DetailHighLightIntent.ToggleComments)
                }
            )
            Text(
                modifier = Modifier.padding(16.dp),
                text = "Highlight",
                style = MaterialTheme.typography.bodyLarge
            )
            ListHighLightsScreen(
                modifier = Modifier.weight(1f),
                onVideoClick = { videoId ->
                    viewModel.sendIntent(DetailHighLightIntent.SetVideoId(videoId))
                },
                highlightViewModel = listViewModel
            )
        }
        AnimatedVisibility(
            visible = isCommentsExpanded,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight }, // Bắt đầu từ dưới màn hình
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight }, // Trượt xuống dưới màn hình
                animationSpec = tween(
                    durationMillis = 250,
                    easing = FastOutLinearInEasing
                )
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomSheet(
                comments = comments,
                videoId = viewModel.videoId,
                onDismiss = {
                    viewModel.sendIntent(DetailHighLightIntent.ToggleComments)
                },
                modifier = Modifier.fillMaxHeight(0.72f)
            )
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            viewModel.sendIntent(DetailHighLightIntent.ClearVideoId)
        }
    }
}

@Composable
fun CommentSection(
    modifier: Modifier = Modifier,
    uiState: DetailHighLightUiState,
    onOpenComment: () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Comments : ${uiState.infoVideo?.commentCount}",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
            ,
        ) {
            /* AsyncImage(
                 modifier = Modifier.size(40.dp).clip(CircleShape),
                 model = painterResource(R.drawable.premier_league),
                 contentDescription = "UserImage",
             )*/
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                imageVector = ImageVector.vectorResource(id = R.drawable.premier_league),
                contentDescription = "UserImage",
            )
            Spacer(Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onOpenComment()
                    }
                    .background(
                        color = Color.LightGray.copy(0.3f),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    modifier = Modifier.padding(8.dp),
                    text = "Thêm bình luận",
                )
            }
        }
    }
}

@Composable
fun CommentsHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Bình luận",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Sort",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Top comments",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun DragHandle(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                // Handle click event
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(32.dp)
                    .height(4.dp)
                    .background(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        shape = RoundedCornerShape(4.dp)
                    )
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Bình luận",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close comments",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

}