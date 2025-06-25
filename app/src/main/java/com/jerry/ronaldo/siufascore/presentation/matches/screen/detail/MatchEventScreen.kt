package com.jerry.ronaldo.siufascore.presentation.matches.screen.detail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.SimCardAlert
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsHandball
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jerry.ronaldo.siufascore.domain.model.MatchEvent
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import kotlinx.coroutines.delay

@Composable
fun MatchEventScreen(
    modifier: Modifier = Modifier,
    uiState: DetailMatchState,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            )
            .padding(horizontal = 16.dp),
    ) {
        if (uiState.events.isEmpty()) {
            EmptyEventsState()
        }else{
            uiState.timelineEvents.forEach {event->
                MatchEventItem(
                    event = event,
                    homeTeamId = uiState.homeTeamId?:0 ,
                    awayTeamId = uiState.awayTeamId?:0
                )
            }
        }
    }
}
@Composable
private fun EmptyEventsState(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.EventNote,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chưa có sự kiện nào",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Các sự kiện của trận đấu sẽ được hiển thị ở đây",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
@Composable
fun MatchEventItem(
    modifier: Modifier = Modifier,
    event: MatchEvent,
    homeTeamId: Int,
    awayTeamId: Int,
) {
    var isVisible by remember { mutableStateOf(false) }
    val isHome = event.team.id == homeTeamId
    LaunchedEffect(event) {
        delay(100)
        isVisible = true
    }
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(300)
        ) + fadeIn(animationSpec = tween(300)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
            ,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isHome) {
                EventContent(
                    event = event,
                    isHome = true,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
            EventTimeLine(
                event = event,
                modifier = Modifier.width(80.dp)
            )
            if (!isHome) {
                EventContent(
                    event = event,
                    isHome = false,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Spacer(Modifier.weight(1f))
            }
        }

    }
}

@Composable
fun EventContent(
    event: MatchEvent,
    isHome: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isHome) Arrangement.End else Arrangement.Start
    ) {
        if (!isHome) {
            EventIcon(event = event)
            Spacer(Modifier.width(8.dp))
        }
        Column(
            horizontalAlignment = if (isHome) Alignment.End else Alignment.Start
        ) {
            Text(
                text = event.player.shortName,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Purple,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (event.assist != null && event.isGoal) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ){
                    Icon(
                        imageVector = Icons.Default.SportsHandball,
                        contentDescription = "Goal",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = event.assist.shortName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }else if(event.isCard){
                Text(
                    text = "Phạm lỗi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }else if (event.isSubstitution){
                Text(
                    text = "Thay người",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        if (isHome) {
            Spacer(Modifier.width(8.dp))
            EventIcon(event = event)
        }
    }
}

@Composable
fun EventTimeLine(
    event: MatchEvent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(20.dp)
                .background(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(1.dp)
                ),
        )
        Box(
            modifier = Modifier
                .background(
                    color = when {
                        event.isGoal -> Color(0xFF4CAF50)
                        event.isRedCard -> Color(0xFFE53E3E)
                        event.isYellowCard -> Color(0xFFFFC107)
                        else -> MaterialTheme.colorScheme.primary
                    },
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = event.displayTime,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )
        }
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(20.dp)
                .background(
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    RoundedCornerShape(1.dp)
                )
        )
    }
}

@Composable
fun EventIcon(
    event: MatchEvent,
    modifier: Modifier = Modifier
) {
    val (icon, color, backgroundColor) = when {
        event.isGoal -> Triple(
            Icons.Default.Sports,
            Color.White,
            Color(0xFF4CAF50)
        )

        event.isYellowCard -> Triple(
            Icons.Default.SimCardAlert,
            Color.Black,
            Color(0xFFFFC107)
        )

        event.isRedCard -> Triple(
            Icons.Default.SimCardAlert,
            Color.White,
            Color(0xFFE53E3E)
        )

        event.isSubstitution -> Triple(
            Icons.Default.SwapHoriz,
            Color.White,
            Color(0xFF2196F3)
        )

        else -> Triple(
            Icons.Default.Info,
            Color.White,
            MaterialTheme.colorScheme.primary
        )
    }
    Box(
        modifier = modifier
            .size(24.dp)
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = event.type.name,
            tint = color,
            modifier = Modifier.size(24.dp)
        )
    }
}