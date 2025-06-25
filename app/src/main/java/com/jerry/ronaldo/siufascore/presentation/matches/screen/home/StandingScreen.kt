package com.jerry.ronaldo.siufascore.presentation.matches.screen.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.presentation.mapper.TeamStandingItem
import com.jerry.ronaldo.siufascore.presentation.matches.MatchesViewModel

@Composable
fun StandingScreen(viewModel: MatchesViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            border = BorderStroke(1.dp, Color.LightGray)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF00BFFF),
                                    Color(0xFF1E90FF)
                                )
                            )
                        )
                        .padding(vertical = 16.dp),
                ) {
                    Text(
                        text = "Bảng xếp hạng",
                        color = Color.White,
                        fontSize = 24.sp,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Color(0xFFF8F9FA)
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Pos",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.weight(0.8f)
                    )
                    Text(
                        text = "Club",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.weight(2f)
                    )
                    Text(
                        text = "PL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "GD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "PTS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.End
                    )
                }
                // val homeListTeam = uiState.standingItem.filter { it.isHighlighted }
                uiState.standingItems.forEach { teamStanding ->
                    TeamStandingScreen(teamStanding)
                    HorizontalDivider(
                        color = Color.LightGray.copy(
                            alpha = 0.5f
                        ),
                        thickness = 1.dp,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .border(
                            width = 1.dp,
                            color = Color.LightGray.copy(
                                alpha = 0.5f
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View full table",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Arrow",
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TeamStandingScreen(teamStanding: TeamStandingItem) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(0.8f), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = teamStanding.position.toString(),
                    fontSize = 16.sp,
                )
                Row(
                    modifier = Modifier
                        .weight(2f)
                        .padding(start = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = teamStanding.teamLogo,
                        contentDescription = null,
                        modifier = Modifier
                            .size(32.dp)
                            .padding(end = 8.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = teamStanding.teamName,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
                Text(
                    text = teamStanding.playedGame.toString(),
                    fontSize = 16.sp,
                    modifier = Modifier.weight(0.8f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = if (teamStanding.goalDifference > 0) "+${teamStanding.goalDifference}"
                    else teamStanding.goalDifference.toString(),
                    fontSize = 16.sp,
                    modifier = Modifier.weight(0.8f),
                    textAlign = TextAlign.End
                )
                Text(
                    text = teamStanding.points.toString(),
                    fontSize = 16.sp,
                    modifier = Modifier.weight(0.8f),
                    textAlign = TextAlign.End,
                    style = MaterialTheme.typography.titleLarge,
                )

            }
        }
    }
}

