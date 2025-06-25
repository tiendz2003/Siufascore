package com.jerry.ronaldo.siufascore.presentation.matches.screen.detail

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.domain.model.Match
import com.jerry.ronaldo.siufascore.presentation.ui.Purple
import com.jerry.ronaldo.siufascore.presentation.ui.Purple80
import com.jerry.ronaldo.siufascore.utils.MatchStatus
import com.jerry.ronaldo.siufascore.utils.extractDateFromUtc
import com.jerry.ronaldo.siufascore.utils.formatDisplayDate

@Composable
fun H2HScreen(
    modifier: Modifier = Modifier,
    matches: List<Match>,
    onMatchClick: (Match) -> Unit
) {
    val matchByDate = matches.groupBy { it.utcDate.extractDateFromUtc() }.toSortedMap()
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        matchByDate.forEach { (date, matches) ->
            Text(
                text = date.formatDisplayDate(),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF2C2C2C)
                ),
                modifier = Modifier.padding(start = 4.dp)
            )
            LazyColumn(
                modifier = Modifier.size(800.dp),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(matches) { match ->
                    H2HCard(match)
                }
            }
        }
    }
}

@Composable
fun H2HCard(
    match: Match,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        border = BorderStroke(1.dp, Purple80)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (match.status == MatchStatus.IN_PLAY.displayName) {
                    match.status
                } else {
                    match.utcDate
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Purple,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.size(32.dp)
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                H2HMatchSection(
                    match = match,
                    isHome = true,
                )

                H2HMatchSection(
                    match = match,
                    isHome = false,
                )
            }
            ScoreSection(
                homeScore = match.score.fullTime.home ?: 0,
                awayScore = match.score.fullTime.away ?: 0
            )
        }
    }

}

@Composable
fun H2HMatchSection(
    match: Match,
    modifier: Modifier = Modifier, isHome: Boolean
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        AsyncImage(
            model = if (isHome) match.homeTeam.crest else match.awayTeam.crest,
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = if (isHome) match.homeTeam.shortName else match.awayTeam.shortName,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Medium,
                color = Color(0xFF2C2C2C)
            ),
        )
    }
}

@Composable
fun ScoreSection(
    homeScore: Int,
    awayScore: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = homeScore.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        Text(
            text = awayScore.toString(),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )
    }
}
