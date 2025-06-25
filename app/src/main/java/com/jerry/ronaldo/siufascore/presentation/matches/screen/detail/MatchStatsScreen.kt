package com.jerry.ronaldo.siufascore.presentation.matches.screen.detail

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp


@Composable
fun MatchStatsScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Possession Section
        PossessionSection(
            homePercentage = 70,
            awayPercentage = 30
        )


        // Match Statistics
        MatchStatistics()

    }
}

@Composable
fun PossessionSection(
    homePercentage: Int,
    awayPercentage: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "possession",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Team Possession
            PossessionCircle(
                percentage = homePercentage,
                color = Color(0xFF6366F1), // Purple/Indigo
                modifier = Modifier.size(80.dp)
            )

            // Away Team Possession
            PossessionCircle(
                percentage = awayPercentage,
                color = Color(0xFF10B981), // Green
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

@Composable
fun PossessionCircle(
    percentage: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    val animateProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "Possession Progress"
    )
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier.fillMaxSize()) {
            val strokeWidth = 8.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2
            val center = Offset(size.width / 2, size.height / 2)
            drawCircle(
                color = color,
                radius = radius,
                center = center,
                style = Stroke(strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = animateProgress * 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                topLeft = Offset(
                    center.x - radius,
                    center.y - radius
                ),
                size = Size(radius * 2, radius * 2)
            )
        }
        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun MatchStatistics() {
    val stats = listOf(
        StatItem("Total shots", 35, 10, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Shots on target", 20, 4, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Shots off target", 15, 6, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Blocked shots", 6, 16, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Corner", 8, 2, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Offsides", 4, 5, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Fouls", 5, 4, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Crosses", 7, 5, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Yellow cards", 4, 7, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Red cards", 0, 0, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Counter attacks", 4, 4, Color(0xFF6366F1), Color(0xFF10B981)),
        StatItem("Goalkeeper saves", 5, 13, Color(0xFF6366F1), Color(0xFF10B981))
    )
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        stats.forEach { stat ->
            StatRow(stat)
        }
    }
}

@Composable
fun StatRow(stat: StatItem) {
    val maxValue = maxOf(stat.homeValue, stat.awayValue).coerceAtLeast(1)
    val homeProgress = stat.homeValue.toFloat() / maxValue
    val awayProgress = stat.awayValue.toFloat() / maxValue
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stat.homeValue.toString(),
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stat.awayValue.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Home Team Bar
            StatProgressBar(
                progress = homeProgress,
                color = stat.homeColor,
                isReversed = true,
                modifier = Modifier.weight(1f)
            )

            // Away Team Bar
            StatProgressBar(
                progress = awayProgress,
                color = stat.awayColor,
                isReversed = false,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatProgressBar(progress: Float, color: Color, isReversed: Boolean, modifier: Modifier) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(800, easing = EaseOutCubic),
        label = "progress"
    )

    Box(
        modifier = modifier.height(8.dp)
    ) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Gray.copy(alpha = 0.2f),
                    RoundedCornerShape(4.dp)
                )
        )

        // Progress
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .background(color, RoundedCornerShape(4.dp))
                .then(
                    if (isReversed) {
                        Modifier.align(Alignment.CenterEnd)
                    } else {
                        Modifier.align(Alignment.CenterStart)
                    }
                )
        )
    }

}

data class StatItem(
    val label: String,
    val homeValue: Int,
    val awayValue: Int,
    val homeColor: Color,
    val awayColor: Color
)
