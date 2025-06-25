package com.jerry.ronaldo.siufascore.presentation.player

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.PlayerTeam
import com.jerry.ronaldo.siufascore.domain.model.PlayerTrophy
import com.jerry.ronaldo.siufascore.presentation.search.screen.PerformanceStatColumn
import com.jerry.ronaldo.siufascore.presentation.search.screen.StatColumn

@Composable
fun OverviewSection(uiState: DetailPlayerUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BioContent(
            modifier = Modifier.padding(horizontal = 16.dp),
            nationality = uiState.nationality,
            nationalityImage = "",
            position = uiState.position,
            age = uiState.age,
            appearances = uiState.number,
            weight = uiState.weight,
            goals = uiState.totalGoals
        )
        PlayerOverviewScreen(
            playerId = 0,
            uiState = uiState
        )
    }
}


@Composable
fun BioContent(
    nationality: String,
    nationalityImage: String,
    position: String,
    age: Int,
    appearances: Int,
    weight: String,
    goals: Int,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    val cardElevation by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 8.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_elevation"
    )

    val cardScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "card_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(cardScale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    }
                )
            },
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2D1B69)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Top section with enhanced styling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    StatColumn(
                        label = "Quốc tịch",
                        value = nationality,
                        leadingIcon = {
                            AsyncImage(
                                model = nationalityImage,
                                contentDescription = null,
                                modifier = Modifier.size(32.dp),
                                placeholder = painterResource(R.drawable.premier_league),
                                error = painterResource(R.drawable.premier_league),
                            )
                        },
                        modifier = Modifier.weight(1f)
                    )

                    StatColumn(
                        label = "Vị trí",
                        value = position,
                        modifier = Modifier.weight(1f)
                    )

                    StatColumn(
                        label = "Tuổi",
                        value = age.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Bottom section with performance stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    PerformanceStatColumn(
                        label = "Ra sân",
                        value = appearances.toString(),
                        icon = Icons.Default.EmojiEvents,
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )

                    PerformanceStatColumn(
                        label = "Cân nặng",
                        value = weight,
                        icon = Icons.Default.Sports,
                        color = Color(0xFF3B82F6),
                        modifier = Modifier.weight(1f)
                    )

                    PerformanceStatColumn(
                        label = "Bàn thắng",
                        value = goals.toString(),
                        icon = Icons.Default.Favorite,
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }

            }
        }
    }
}

@Composable
fun PlayerOverviewScreen(
    playerId: Int,
    uiState: DetailPlayerUiState
) {

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            uiState.isOverviewLoading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.overviewError != null -> {
                ErrorContent(
                    message = uiState.overviewError,
                    onRetry = {

                    },
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            uiState.hasOverviewData -> {
                uiState.playerOverview?.let {
                    PlayerTimelineContent(
                        timelineItems = it,
                        modifier = Modifier.fillMaxSize()
                    )
                }

            }
        }
    }
}

@Composable
fun PlayerTimelineContent(
    timelineItems: List<TimelineItem>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        timelineItems.forEach { item ->
            TimelineItemCard(
                item = item,
                isLast = item == timelineItems.last()
            )
        }
    }
}

@Composable
fun TimelineItemCard(
    item: TimelineItem,
    isLast: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        // Timeline indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(48.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(100.dp)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Content
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // Season Header
            Text(
                text = "Mùa giải ${item.season}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Teams
            if (item.teams.isNotEmpty()) {
                TeamsSection(teams = item.teams)
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Trophies
            if (item.trophies.isNotEmpty()) {
                TrophiesSection(trophies = item.trophies)
            }
        }
    }
}


@Composable
fun TeamsSection(
    teams: List<PlayerTeam>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Câu lạc bộ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        teams.forEach { team ->
            TeamCard(team = team)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TeamCard(
    team: PlayerTeam,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = team.teamLogo,
                contentDescription = "Logo ${team.teamName}",
                modifier = Modifier.size(32.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = team.teamName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun TrophiesSection(
    trophies: List<PlayerTrophy>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Danh hiệu (${trophies.size})",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(trophies) { trophy ->
                TrophyCard(trophy = trophy)
            }
        }
    }
}

@Composable
fun TrophyCard(
    trophy: PlayerTrophy,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(180.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Trophy",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = trophy.place,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = trophy.league,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = trophy.country,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(48.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = onRetry) {
            Text("Thử lại", style = MaterialTheme.typography.bodyLarge)
        }
    }
}