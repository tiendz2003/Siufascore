package com.jerry.ronaldo.siufascore.presentation.highlight.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.presentation.highlight.DetailHighLightIntent
import com.jerry.ronaldo.siufascore.presentation.highlight.DetailHighLightUiState
import com.jerry.ronaldo.siufascore.presentation.highlight.DetailHighlightViewModel
import com.jerry.ronaldo.siufascore.presentation.highlight.HighLightIntent
import com.jerry.ronaldo.siufascore.presentation.highlight.HighlightViewModel
import com.jerry.ronaldo.siufascore.presentation.ui.BottomSheet
import com.jerry.ronaldo.siufascore.utils.League
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

    // State cho dropdown
    var selectedLeague by remember { mutableStateOf<League?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val highlights = listViewModel.playlistVideo.collectAsLazyPagingItems()

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

    Box(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        val lazyListState = rememberLazyListState()
        val shouldShowStickyDropdown by remember {
            derivedStateOf {
                lazyListState.firstVisibleItemIndex > 3 ||
                        (lazyListState.firstVisibleItemIndex == 3 && lazyListState.firstVisibleItemScrollOffset > 0)
            }
        }
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            YoutubePlayerScreen(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(videoAspectRatio),
                videoId = uiState.videoId ?: viewModel.videoId,
                onCurrentSecondChanged = { second ->
                    // Handle current second changed
                }
            )

            AnimatedVisibility(
                visible = shouldShowStickyDropdown,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(500)
                ) + fadeIn(animationSpec = tween(500)),
                exit = slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(500)
                ) + fadeOut(animationSpec = tween(500)),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 8.dp,
                            spotColor = Color.Black.copy(alpha = 0.1f)
                        ),
                    color = Color.White
                ) {
                    LeagueDropdownMenu(
                        selectedLeague = selectedLeague,
                        onLeagueSelected = { league ->
                            selectedLeague = league
                            listViewModel.sendIntent(HighLightIntent.SetPlaylistId(league.id))
                        },
                        onExpandedChange = {
                            isDropdownExpanded = it
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    )
                }
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Video Info Section
                item {
                    VideoInfoSection(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        title = uiState.infoVideo?.title ?: "Không rõ tiêu đề",
                        viewCount = uiState.infoVideo?.viewCount ?: "Không rõ lượt xem",
                        timeAgo = uiState.infoVideo?.publishedAt ?: "Không rõ ngày đăng"
                    )
                }

                // Divider
                item {
                    HorizontalDivider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .padding(horizontal = 16.dp)
                    )
                }
                item {
                    CommentSection(
                        modifier = Modifier.padding(16.dp),
                        uiState = uiState,
                        onOpenComment = {
                            viewModel.sendIntent(DetailHighLightIntent.ToggleComments)
                        }
                    )
                }
                item {
                    Column {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = "Highlight liên quan",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontSize = 18.sp
                            )
                        )
                    }
                }
                items(
                    count = highlights.itemCount,
                ) { index ->
                    val item = highlights[index]
                    item?.let {
                        VideoCard(
                            video = it,
                            onVideoClick = {
                                if (!isDropdownExpanded) {
                                    viewModel.sendIntent(DetailHighLightIntent.SetVideoId(it.videoId))
                                }
                            },
                            enabled = !isDropdownExpanded,
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .then(
                                    if (isDropdownExpanded) {
                                        Modifier.alpha(0.5f)
                                    } else {
                                        Modifier
                                    }
                                )
                        )
                    }
                }
            }
        }

        if (isDropdownExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        isDropdownExpanded = false
                    }
            )
        }
        AnimatedVisibility(
            visible = isCommentsExpanded,
            enter = slideInVertically(
                initialOffsetY = { fullHeight -> fullHeight },
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                )
            ),
            exit = slideOutVertically(
                targetOffsetY = { fullHeight -> fullHeight },
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
                .fillMaxWidth(),
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