package com.jerry.ronaldo.siufascore.presentation.matches.screen.item

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.Team
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.utils.MatchStatus
import com.jerry.ronaldo.siufascore.utils.WinnerSide
import com.jerry.ronaldo.siufascore.utils.shimmerEffect
import com.jerry.ronaldo.siufascore.utils.whoIsWinner
import timber.log.Timber

@Composable
fun MatchItem(match: Match, onMatchClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    color = Purple,
                    radius = 12.dp
                )
            ) {
                onMatchClick(match.id)
            }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(0.4f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                modifier = Modifier.weight(1f, fill = false).padding(end = 4.dp),
                text = match.homeTeam.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = Purple,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End
            )
            AsyncImage(
                model = match.homeTeam.logo,
                onError = {
                    Timber.tag("MatchesScreen").d("Error loading image: ${it.result.throwable}")
                },
                placeholder = painterResource(R.drawable.ic_launcher_foreground),
                contentDescription = "${match.homeTeam} logo",
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
        }
        Box(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .weight(0.2f)
                .background(
                    color = Purple,
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${match.score.fulltime.home}  -  ${match.score.fulltime.away}",
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
        Row(
            modifier = Modifier.weight(0.4f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            AsyncImage(
                model = match.awayTeam.logo,
                contentDescription = "${match.awayTeam} logo",
                modifier = Modifier
                    .size(50.dp)
                    .padding(8.dp),
            )

            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = match.awayTeam.name,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 12.sp,
                color = Purple,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start
            )

        }

    }

}


@Composable
fun MatchLiveItem(
    modifier: Modifier = Modifier,
    matchId: Int,
    homeTeam: Team,
    awayTeam: Team,
    homeScore: Int?,
    awayScore: Int?,
    matchTime: String,
    venue: String,
   status: MatchStatus = MatchStatus.SCHEDULED,
    onMatchClick: (Int) -> Unit = {},
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                onMatchClick(matchId)
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF8F8FB)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 3.dp
        ),
        border = BorderStroke(
            brush = Brush.horizontalGradient(
                listOf(
                    Purple,
                    Purple.copy(0.7f)
                )
            ),
            width = 1.5.dp
        )
    ) {
        Box(
            modifier = Modifier

                .fillMaxWidth()

                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFFFFF), // Trắng nhẹ
                            Color(0xFFF1F1F6), // Xám nhạt
                            Color(0xFFE6E6FA).copy(alpha = 0.5f)
                        ),
                        radius = 800f
                    )
                )
        ) {
            Image(
                modifier = Modifier
                    .width(280.dp)
                    .height(165.dp), // Đặt height cố định
                painter = painterResource(R.drawable.bg_match_live),
                contentDescription = "livestream_card",
                contentScale = ContentScale.Crop // Cắt ảnh để fit
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TeamSection(
                        team = homeTeam,
                        modifier = Modifier.width(80.dp)
                    )
                    ScoreSection(
                        homeScore = homeScore,
                        awayScore = awayScore,
                    )
                    TeamSection(
                        team = awayTeam,
                        modifier = Modifier.width(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                MatchTimeChip(time = matchTime, status = status)
                Spacer(modifier = Modifier.height(12.dp))
                VenueInfo(venue = venue)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun TeamSection(
    team: Team,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = team.logo,
            contentDescription = team.name,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape),
            placeholder = painterResource(R.drawable.premier_league),
            error = painterResource(R.drawable.premier_league),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = team.name,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 14.sp,
                color = Color.White
            ),
        )
    }
}

@Composable
private fun ScoreSection(
    homeScore: Int?,
    awayScore: Int?,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ScoreDisplay(
            score = homeScore,
            isWinner = whoIsWinner(homeScore, awayScore) == WinnerSide.HOME
        )
        Text(
            text = "-",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Light,
                fontSize = 24.sp
            ),
            color = Color.White
        )
        ScoreDisplay(
            score = awayScore,
            isWinner = whoIsWinner(homeScore, awayScore) == WinnerSide.AWAY
        )
    }
}

@Composable
private fun ScoreDisplay(
    score: Int?,
    isWinner: Boolean
) {
    val backgroundColor = if (isWinner && score != null) {
        Color.White.copy(0.2f)
    } else {
        Color.Transparent
    }
    val textColor = if (isWinner && score != null) {
        Color.White
    } else {
        Color.White.copy(0.5f)
    }
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = score?.toString() ?: "-",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 20.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun MatchTimeChip(
    time: String,
    status: MatchStatus
) {
    val (backgroundColor, textColor) = when (status) {
        MatchStatus.SCHEDULED -> Color(0xFF2A2A2A) to Color.White         // Xám đậm - Chưa bắt đầu
        MatchStatus.TIMED -> Color(0xFF607D8B) to Color.White             // Xanh xám - Có giờ cụ thể
        MatchStatus.POSTPONED -> Color(0xFFFFC107) to Color.Black        // Vàng - Bị hoãn
        MatchStatus.IN_PLAY -> Color(0xFFFF4444) to Color.White          // Đỏ - Đang diễn ra
        MatchStatus.PAUSED -> Color(0xFFFFC107) to Color.White           // Tím - Nghỉ giữa hiệp
        MatchStatus.FT -> Color(0xFF1E90FF) to Color.White         // Xanh lá - Kết thúc
        MatchStatus.SUSPENDED -> Color(0xFF795548) to Color.White        // Nâu - Tạm dừng (do sự cố)
        MatchStatus.CANCELLED -> Color(0xFFBDBDBD) to Color.Black        // Xám nhạt - Hủy bỏ
    }
    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (status == MatchStatus.IN_PLAY) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = Color.White,
                            shape = CircleShape
                        )
                        .shimmerEffect()
                )
            }
            Text(
                text = status.displayName,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = textColor
            )
        }
    }
}

@Composable
private fun VenueInfo(venue: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.location),
            contentDescription = "Venue Icon",
            modifier = Modifier.size(14.dp),
            tint = Color.Red
        )
        Text(
            text = venue,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp,
                fontSize = 12.sp
            ),
            color =Color.White
        )
    }
}