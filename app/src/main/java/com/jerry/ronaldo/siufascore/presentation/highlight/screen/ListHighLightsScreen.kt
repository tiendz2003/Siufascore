package com.jerry.ronaldo.siufascore.presentation.highlight.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.VideoItem
import com.jerry.ronaldo.siufascore.presentation.highlight.HighLightIntent
import com.jerry.ronaldo.siufascore.presentation.highlight.HighlightViewModel
import com.jerry.ronaldo.siufascore.presentation.ui.LeagueDropdownItem
import com.jerry.ronaldo.siufascore.utils.League
import com.jerry.ronaldo.siufascore.utils.LeagueData
import com.jerry.ronaldo.siufascore.utils.formatViewCount
import kotlin.random.Random

@Composable
fun ListHighLightsScreen(
    modifier: Modifier = Modifier,
    onVideoClick: (String) -> Unit,
    highlightViewModel: HighlightViewModel = hiltViewModel()
) {
    val highlights = highlightViewModel.playlistVideo.collectAsLazyPagingItems()
    var selectedLeague by remember { mutableStateOf<League?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    Box(
        modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        )
        {
            LeagueDropdownMenu(
                selectedLeague = selectedLeague,
                onLeagueSelected = { league ->
                    selectedLeague = league
                    highlightViewModel.sendIntent(HighLightIntent.SetPlaylistId(league.id))
                },
                onExpandedChange = {
                    isDropdownExpanded = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (isDropdownExpanded) {
                            Modifier.pointerInput(Unit) {
                                // Consume all pointer events
                                detectTapGestures { }
                            }
                        } else {
                            Modifier
                        }
                    )
                    .alpha(
                        if (isDropdownExpanded) 0.5f else 1f
                    )
            ) {
                items(
                    count = highlights.itemCount,
                    key = { index ->
                        highlights[index]?.videoId ?: "empty_$index"
                    }
                ) { index ->
                    val item = highlights[index]
                    item?.let {
                        VideoCard(
                            video = it,
                            onVideoClick = {
                                if (!isDropdownExpanded) {
                                    onVideoClick(it.videoId)
                                }
                            },
                            enabled = !isDropdownExpanded
                        )
                    }
                }
            }
        }
        if (isDropdownExpanded) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 56.dp) // Height của dropdown trigger
                    .background(Color.Black.copy(alpha = 0.1f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        // Click overlay to close dropdown - optional
                        isDropdownExpanded = false
                    }
            )
        }
    }
}

@Composable
fun VideoCard(
    modifier: Modifier = Modifier,
    video: VideoItem,
    onVideoClick: () -> Unit,
    enabled: Boolean = true
) {
    val views by remember(
        video.videoId
    ) { mutableStateOf(Random.nextInt().coerceIn(100000, 10000000).toString()) }
    Card(
        modifier = modifier.alpha(if (enabled) 1f else 0.6f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        onClick = {
            onVideoClick()
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ThumbnailSection(
                modifier = Modifier.size(width = 150.dp, height = 80.dp),
                thumbnailUrl = video.thumbnailUrl,
                duration = "10:39"
            )
            VideoInfoSection(
                modifier = Modifier.weight(1f),
                title = video.title,
                viewCount = views.formatViewCount(),
                timeAgo = video.publishedAt,
            )
        }
    }
}

@Composable
fun ThumbnailSection(
    modifier: Modifier = Modifier,
    thumbnailUrl: String,
    duration: String,
) {
    Box(
        modifier = modifier.clip(RoundedCornerShape(8.dp))
    ) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = thumbnailUrl,
            contentScale = ContentScale.FillBounds,
            contentDescription = "thumbnailUrl",
            placeholder = painterResource(R.drawable.premier_league),
            error = painterResource(R.drawable.premier_league),
        )
        DurationBox(
            duration = duration,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }

}
@Composable
private fun DurationBox(
    duration: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(6.dp)
            .background(
                color = Color.Black.copy(0.7f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = duration,
            style = MaterialTheme.typography.labelSmall.copy(
                color = Color.White,
                fontWeight = FontWeight.Medium
            ),
            fontSize = 12.sp
        )
    }
}
@Composable
fun VideoInfoSection(
    modifier: Modifier = Modifier,
    title: String,
    viewCount: String,
    timeAgo: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Medium,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = viewCount,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 13.sp,
                    color = Color.Gray,
                    fontWeight = FontWeight.Normal,
                )
            )
            Box(
                modifier = Modifier
                    .size(2.dp)
                    .background(
                        color = Color.Gray,
                        shape = CircleShape
                    )
            )
            Text(
                text = timeAgo,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 13.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Normal,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeagueDropdownMenu(
    selectedLeague: League?,
    onLeagueSelected: (League) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {

    var expanded by remember { mutableStateOf(false) }
    val leagues = LeagueData.leagues
    LaunchedEffect(expanded) {
        onExpandedChange(expanded)
    }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            if (enabled) expanded = it
        },
        modifier = modifier
    ) {
        // Trigger - Custom styled
        LeagueDropdownTrigger(
            selectedLeague = selectedLeague,
            expanded = expanded,
            enabled = enabled,
            onClick = {
                if (enabled) expanded = !expanded
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    if (enabled) expanded = !expanded
                }
        )

        // Dropdown Menu
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 400.dp)
        ) {
            leagues.forEach { league ->
                LeagueDropdownItem(
                    league = league,
                    isSelected = selectedLeague?.id == league.id,
                    onClick = {
                        onLeagueSelected(league)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LeagueDropdownTrigger(
    selectedLeague: League?,
    expanded: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        color = if (enabled) {
            MaterialTheme.colorScheme.surface
        } else {
            MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
        },
        border = BorderStroke(
            width = 1.dp,
            color = if (expanded) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            }
        ),
        shadowElevation = if (expanded) 4.dp else 1.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (selectedLeague != null) {
                    // League Icon
                    Image(
                        imageVector = ImageVector.vectorResource(selectedLeague.iconRes),
                        contentDescription = selectedLeague.name,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                    Column {
                        Text(
                            text = selectedLeague.name,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = if (enabled) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            }
                        )
                        Text(
                            text = selectedLeague.country,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = if (enabled) 0.7f else 0.4f
                            )
                        )
                    }
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sports,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "Chọn giải đấu",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            // Dropdown Arrow
            Icon(
                imageVector = if (expanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = if (expanded) "Collapse" else "Expand",
                modifier = Modifier
                    .size(24.dp)
                    .rotate(
                        animateFloatAsState(
                            targetValue = if (expanded) 180f else 0f,
                            animationSpec = tween(200),
                            label = "arrow_rotation"
                        ).value
                    ),
                tint = if (enabled) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                }
            )
        }
    }
}