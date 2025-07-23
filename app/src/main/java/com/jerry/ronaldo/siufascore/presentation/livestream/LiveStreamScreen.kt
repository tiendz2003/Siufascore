package com.jerry.ronaldo.siufascore.presentation.livestream

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.domain.model.Team
import com.jerry.ronaldo.siufascore.presentation.favorite.Loading
import com.jerry.ronaldo.siufascore.presentation.ui.PremierPurpleDark


@Composable
fun LiveStreamScreen(
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit,
    liveStreamViewModel: LiveStreamViewModel = hiltViewModel(),
) {
    val uiState by liveStreamViewModel.uiState.collectAsStateWithLifecycle()
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            when {
                uiState.isLoading -> {
                    Loading()
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage ?: "Đã có lỗi",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }

                else -> {
                    LiveStreamListScreen(
                        matches = uiState.matches,
                        onMatchClick = {matchId->
                            onClick(matchId)
                        }
                    )
                }
            }
        }
    }

}

@Composable
fun LiveStreamListScreen(
    modifier: Modifier = Modifier,
    matches: List<Match>, onMatchClick: (Int) -> Unit = {}
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        items(matches, key = {
            it.id
        }) { match ->
            MatchCard(match = match,onMatchClick = { matchId->
                onMatchClick(matchId)
            })
        }
    }
}

@Composable
fun MatchCard(
    match: Match,
    onMatchClick:(Int) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp).clickable {
                onMatchClick(match.id)
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp,
            hoveredElevation = 6.dp,
            focusedElevation = 6.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    color = PremierPurpleDark
                )
        ) {
            Image(
                painter = painterResource(R.drawable.bg_match_live),
                contentDescription = "Match Logo",
                modifier = Modifier
                    .fillMaxSize()

            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Tournament header
                LeagueHeader(
                    leagueName = match.league.name,
                    country = match.league.country
                )

                // Match content
                MatchContent(match = match)

                // Match week
                MatchWeek(matchWeek = match.venue.name)
            }
        }
    }
}

@Composable
private fun LeagueHeader(
    leagueName: String,
    country: String,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = leagueName,
            color = Color.White,
            style = MaterialTheme.typography.titleLarge,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Text(
            text = country,
            color = Color.White.copy(alpha = 0.8f),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun MatchContent(match: Match) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Home team
        TeamLiveSection(
            modidier = Modifier.weight(1f).padding(end = 16.dp),
            team = match.homeTeam,
            alignment = Alignment.CenterHorizontally
        )

        // Score
        ScoreLiveSection(
            homeScore = match.score.fulltime.home ?: 0,
            awayScore = match.score.fulltime.away ?: 0
        )

        // Away team
        TeamLiveSection(
            modidier = Modifier.weight(1f).padding(start = 16.dp),
            team = match.awayTeam,
            alignment = Alignment.CenterHorizontally
        )
    }
}

@Composable
private fun TeamLiveSection(
    team: Team,
    alignment: Alignment.Horizontal, modidier: Modifier = Modifier
) {
    Column(
        modifier = modidier,
        horizontalAlignment = alignment,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Team logo or flag
        Box(
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = team.logo,
                contentDescription = "Team Logo",
                placeholder = painterResource(R.drawable.premier_league),
                error = painterResource(R.drawable.premier_league),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }

        Text(
            text = team.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ScoreLiveSection(
    homeScore: Int,
    awayScore: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = homeScore.toString(),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = ":",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = awayScore.toString(),
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MatchWeek(matchWeek: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color.White.copy(alpha = 0.2f),
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Text(
                text = matchWeek,
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}

