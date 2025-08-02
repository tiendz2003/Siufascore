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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
import com.jerry.ronaldo.siufascore.domain.model.MatchStatistics
import com.jerry.ronaldo.siufascore.domain.model.StatComparison


@Composable
fun MatchStatsScreen(
    modifier: Modifier = Modifier,
    homeTeamName: String = "Home",
    awayTeamName: String = "Away",
    statistics: MatchStatistics?,
) {
    if (statistics == null) {
        // Loading or no data state
        NoStatsAvailable(modifier = modifier)
        return
    }
    val comparisonStats = statistics.getComparisonStats()
    Column(
        modifier = modifier
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        TeamHeaders(
            homeTeamName = homeTeamName,
            awayTeamName = awayTeamName
        )


        val possessionStat = comparisonStats.find { it.label == "Ball Possession" }
        if (possessionStat != null) {
            PossessionSection(
                homePercentage = possessionStat.homeProgress.toInt(),
                awayPercentage = possessionStat.awayProgress.toInt(),
                homeTeamName = homeTeamName,
                awayTeamName = awayTeamName
            )
        }


        Text(
            text = "Thống kê trận đấu",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )


        comparisonStats.forEach { stat ->
            StatRow(
                stat = stat,
                homeTeamColor = Color(0xFF6366F1), // Purple/Indigo
                awayTeamColor = Color(0xFF10B981)  // Green
            )
        }

        DetailedStatsSection(statistics = statistics)

    }
}

@Composable
private fun TeamHeaders(
    homeTeamName: String,
    awayTeamName: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home team
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = homeTeamName,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF6366F1),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "VS",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Away team
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = awayTeamName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 20.sp
                ),
                color = Color(0xFF10B981),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun PossessionSection(
    homePercentage: Int,
    awayPercentage: Int,
    homeTeamName: String,
    awayTeamName: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Kiểm soát bóng",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp
                ),
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team Possession
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PossessionCircle(
                        percentage = homePercentage,
                        color = Color(0xFF6366F1),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = homeTeamName,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6366F1),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Away Team Possession
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    PossessionCircle(
                        percentage = awayPercentage,
                        color = Color(0xFF10B981),
                        modifier = Modifier.size(100.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = awayTeamName,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 18.sp
                        ),
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp
            ),
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}


@Composable
fun StatRow(
    stat: StatComparison,
    homeTeamColor: Color,
    awayTeamColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Stat label
            Text(
                text = stat.label,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp
                ),
                color = Color.Black,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            // Values row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stat.homeValue,
                    style = MaterialTheme.typography.titleLarge,
                    color = homeTeamColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(60.dp),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = stat.awayValue,
                    style = MaterialTheme.typography.titleLarge,
                    color = awayTeamColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(60.dp),
                    textAlign = TextAlign.Center
                )
            }

            // Progress bars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home Team Bar
                StatProgressBar(
                    progress = stat.homeProgress / 100f,
                    color = homeTeamColor,
                    isReversed = true,
                    modifier = Modifier.weight(1f)
                )

                // Center divider
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(8.dp)
                        .background(Color.Gray.copy(alpha = 0.3f))
                )

                // Away Team Bar
                StatProgressBar(
                    progress = stat.awayProgress / 100f,
                    color = awayTeamColor,
                    isReversed = false,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}


@Composable
fun StatProgressBar(
    progress: Float,
    color: Color,
    isReversed: Boolean,
    modifier: Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(1000, easing = EaseOutCubic),
        label = "stat_progress"
    )

    Box(
        modifier = modifier.height(8.dp)
    ) {
        // Background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color.Gray.copy(alpha = 0.15f),
                    RoundedCornerShape(4.dp)
                )
        )

        // Progress bar
        val progressWidth = animatedProgress.coerceAtLeast(0.05f) // Minimum visible progress

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progressWidth)
                .background(
                    color.copy(alpha = 0.8f),
                    RoundedCornerShape(4.dp)
                )
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

@Composable
private fun DetailedStatsSection(statistics: MatchStatistics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Gray.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Thống kê bổ sung",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 16.sp
                ),
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Additional stats that might not be in comparison
            val homeStats = statistics.homeTeamStats
            val awayStats = statistics.awayTeamStats

            DetailedStatRow(
                label = "Cú sút bị chặn",
                homeValue = homeStats.shotsBlocked?.toString() ?: "0",
                awayValue = awayStats.shotsBlocked?.toString() ?: "0"
            )

            DetailedStatRow(
                label = "Dứt điểm trong vòng cấm",
                homeValue = homeStats.shotsInsideBox?.toString() ?: "0",
                awayValue = awayStats.shotsInsideBox?.toString() ?: "0"
            )

            DetailedStatRow(
                label = "Dứt điểm ngoài vòng cấm",
                homeValue = homeStats.shotsOutsideBox?.toString() ?: "0",
                awayValue = awayStats.shotsOutsideBox?.toString() ?: "0"
            )

            DetailedStatRow(
                label = "Việt vị",
                homeValue = homeStats.offsides?.toString() ?: "0",
                awayValue = awayStats.offsides?.toString() ?: "0"
            )

            DetailedStatRow(
                label = "Thẻ vàng",
                homeValue = homeStats.yellowCards?.toString() ?: "0",
                awayValue = awayStats.yellowCards?.toString() ?: "0"
            )

            DetailedStatRow(
                label = "Thẻ đỏ",
                homeValue = homeStats.redCards?.toString() ?: "0",
                awayValue = awayStats.redCards?.toString() ?: "0"
            )

            DetailedStatRow(
                label = "Cứu thua",
                homeValue = homeStats.goalkeeperSaves?.toString() ?: "0",
                awayValue = awayStats.goalkeeperSaves?.toString() ?: "0"
            )

            DetailedStatRow(
                label = "Tổng đường chuyền",
                homeValue = homeStats.totalPasses?.toString() ?: "0",
                awayValue = awayStats.totalPasses?.toString() ?: "0"
            )

            DetailedStatRow(
                label = "Chuyền thành công",
                homeValue = homeStats.passesAccurate?.toString() ?: "0",
                awayValue = awayStats.passesAccurate?.toString() ?: "0"
            )
        }
    }
}

@Composable
private fun DetailedStatRow(
    label: String,
    homeValue: String,
    awayValue: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = homeValue,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF6366F1),
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = awayValue,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF10B981),
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NoStatsAvailable(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.BarChart,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Chưa có thống kê",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Text(
                text = "Thống kê sẽ được cập nhật sau khi trận đấu kết thúc",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}


data class StatItem(
    val label: String,
    val homeValue: Int,
    val awayValue: Int,
    val homeColor: Color,
    val awayColor: Color
)
