package com.jerry.ronaldo.siufascore.presentation.team

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.data.model.Biggest
import com.jerry.ronaldo.siufascore.data.model.Fixtures
import com.jerry.ronaldo.siufascore.data.model.Goals
import com.jerry.ronaldo.siufascore.data.model.HomeAwayTotalInt
import com.jerry.ronaldo.siufascore.data.model.Lineup
import com.jerry.ronaldo.siufascore.data.model.Penalty
import com.jerry.ronaldo.siufascore.data.model.PenaltyDetail
import com.jerry.ronaldo.siufascore.data.model.ResponseTeamStatistics

@Composable
fun TeamStatisticsScreen(
    statistics: ResponseTeamStatistics,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header với thông tin team và league
        item {
            TeamHeaderCard(
                team = statistics.team,
                league = statistics.league,
                form = statistics.form
            )
        }

        // Fixtures Statistics
        item {
            FixturesStatisticsCard(fixtures = statistics.fixtures)
        }

        // Goals Statistics
        item {
            GoalsStatisticsCard(goals = statistics.goals)
        }

        // Performance Metrics
        item {
            PerformanceMetricsCard(
                cleanSheet = statistics.cleanSheet,
                failedToScore = statistics.failedToScore,
                penalties = statistics.penalty
            )
        }

        // Biggest Records
        item {
            BiggestRecordsCard(biggest = statistics.biggest)
        }

        // Formations
        item {
            FormationsCard(lineups = statistics.lineups)
        }
    }
}

@Composable
private fun TeamHeaderCard(
    team: com.jerry.ronaldo.siufascore.domain.model.TeamInfo,
    league: com.jerry.ronaldo.siufascore.domain.model.LeagueInfo,
    form: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Team Logo
            AsyncImage(
                model = team.logo,
                contentDescription = "Team Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Team Name
            Text(
                text = team.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            // League Info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                AsyncImage(
                    model = league.logo,
                    contentDescription = "League Logo",
                    modifier = Modifier.size(24.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = league.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Form
            FormChip(form = form)
        }
    }
}

@Composable
private fun FormChip(form: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Form:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            form.forEach { result ->
                val color = when (result) {
                    'W' -> Color(0xFF4CAF50) // Green
                    'D' -> Color(0xFFFF9800) // Orange
                    'L' -> Color(0xFFF44336) // Red
                    else -> Color.Gray
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(color, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = result.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FixturesStatisticsCard(fixtures: Fixtures) {
    StatisticsCard(
        title = "Thống kê Lịch thi đấu",
        icon = Icons.Default.Sports
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Pie Chart for Win/Draw/Loss
            FixturesChart(fixtures = fixtures)

            Spacer(modifier = Modifier.height(8.dp))

            // Detailed Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                FixtureStatItem(
                    label = "Đã chơi",
                    stats = fixtures.played,
                    color = MaterialTheme.colorScheme.primary
                )
                FixtureStatItem(
                    label = "Trận thắng",
                    stats = fixtures.wins,
                    color = Color(0xFF4CAF50)
                )
                FixtureStatItem(
                    label = "Hòa",
                    stats = fixtures.draws,
                    color = Color(0xFFFF9800)
                )
                FixtureStatItem(
                    label = "Trận thua",
                    stats = fixtures.loses,
                    color = Color(0xFFF44336)
                )
            }
        }
    }
}

@Composable
private fun FixturesChart(fixtures: Fixtures) {
    val total = fixtures.played.total.toFloat()
    val wins = fixtures.wins.total.toFloat()
    val draws = fixtures.draws.total.toFloat()
    val losses = fixtures.loses.total.toFloat()

    if (total > 0) {
        Canvas(
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        ) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2f - 20.dp.toPx()

            var startAngle = 0f

            // Wins
            val winsAngle = (wins / total) * 360f
            drawArc(
                color = Color(0xFF4CAF50),
                startAngle = startAngle,
                sweepAngle = winsAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
            startAngle += winsAngle

            // Draws
            val drawsAngle = (draws / total) * 360f
            drawArc(
                color = Color(0xFFFF9800),
                startAngle = startAngle,
                sweepAngle = drawsAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
            startAngle += drawsAngle

            // Losses
            val lossesAngle = (losses / total) * 360f
            drawArc(
                color = Color(0xFFF44336),
                startAngle = startAngle,
                sweepAngle = lossesAngle,
                useCenter = true,
                topLeft = Offset(center.x - radius, center.y - radius),
                size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2)
            )
        }
    }
}

@Composable
private fun FixtureStatItem(
    label: String,
    stats: HomeAwayTotalInt,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape)
        )

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "${stats.total}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "H: ${stats.home} | A: ${stats.away}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun GoalsStatisticsCard(goals: Goals) {
    StatisticsCard(
        title = "Thống kê bàn thắng",
        icon = Icons.Default.SportsSoccer
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            GoalStatColumn(
                title = "Ghi ",
                stats = goals.forGoals.total,
                color = Color(0xFF4CAF50),
                isPositive = true
            )

            HorizontalDivider(
                modifier = Modifier
                    .height(80.dp)
                    .width(1.dp),
                color = MaterialTheme.colorScheme.outline
            )

            GoalStatColumn(
                title = "Bàn thua",
                stats = goals.against.total,
                color = Color(0xFFF44336),
                isPositive = false
            )
        }
    }
}

@Composable
private fun GoalStatColumn(
    title: String,
    stats: HomeAwayTotalInt,
    color: Color,
    isPositive: Boolean
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = if (isPositive) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(24.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "${stats.total}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Sân nhà",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${stats.home}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Sân khách",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${stats.away}",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun PerformanceMetricsCard(
    cleanSheet: HomeAwayTotalInt,
    failedToScore: HomeAwayTotalInt,
    penalties: Penalty
) {
    StatisticsCard(
        title = "Thống kê hiệu suất",
        icon = Icons.Default.Analytics
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MetricItem(
                    title = "Giữ sạch lưới",
                    value = cleanSheet.total,
                    icon = Icons.Default.Shield,
                    color = Color(0xFF2196F3)
                )

                MetricItem(
                    title = "Bỏ lỡ",
                    value = failedToScore.total,
                    icon = Icons.Default.Block,
                    color = Color(0xFFFF5722)
                )
            }

            Divider()

            // Penalty Statistics
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Thống kê Penalty",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    PenaltyStatItem(
                        label = "Ghi bàn",
                        detail = penalties.scored,
                        color = Color(0xFF4CAF50)
                    )

                    PenaltyStatItem(
                        label = "Bỏ lỡ",
                        detail = penalties.missed,
                        color = Color(0xFFF44336)
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tổng",
                            style = MaterialTheme.typography.labelMedium
                        )
                        Text(
                            text = "${penalties.total}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricItem(
    title: String,
    value: Int,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(32.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = "$value",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun PenaltyStatItem(
    label: String,
    detail: PenaltyDetail,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium
        )

        Text(
            text = "${detail.total}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )

        Text(
            text = detail.percentage,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BiggestRecordsCard(biggest: Biggest) {
    StatisticsCard(
        title = "Kỷ lục & Chuỗi trận",
        icon = Icons.Default.EmojiEvents
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Streak Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Chuỗi trận hiện tại",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StreakItem("Thắng", biggest.streak.wins, Color(0xFF4CAF50))
                        StreakItem("Hòa", biggest.streak.draws, Color(0xFFFF9800))
                        StreakItem("Thua", biggest.streak.loses, Color(0xFFF44336))
                    }
                }
            }

            // Biggest Wins/Losses
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            tint = Color(0xFF4CAF50)
                        )

                        Text(
                            text = "Thắng đậm nhất:",
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "H: ${biggest.wins.home}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "A: ${biggest.wins.away}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingDown,
                            contentDescription = null,
                            tint = Color(0xFFF44336)
                        )

                        Text(
                            text = "Thua đậm ",
                            style = MaterialTheme.typography.labelMedium,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "H: ${biggest.loses.home}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "A: ${biggest.loses.away}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StreakItem(
    label: String,
    value: Int,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun FormationsCard(lineups: List<Lineup>) {
    StatisticsCard(
        title = "Formations Used",
        icon = Icons.Default.FormatListNumbered
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            lineups.forEach { lineup ->
                FormationItem(
                    formation = lineup.formation,
                    played = lineup.played,
                    maxPlayed = lineups.maxOfOrNull { it.played } ?: 1
                )
            }
        }
    }
}

@Composable
private fun FormationItem(
    formation: String,
    played: Int,
    maxPlayed: Int
) {
    val progress = if (maxPlayed > 0) played.toFloat() / maxPlayed.toFloat() else 0f

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = formation,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "$played games",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}

@Composable
private fun StatisticsCard(
    title: String,
    icon: ImageVector,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )

                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            content()
        }
    }
}