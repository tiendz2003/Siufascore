package com.jerry.ronaldo.siufascore.presentation.matches.screen.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.Player
import com.jerry.ronaldo.siufascore.domain.model.PlayerPosition
import com.jerry.ronaldo.siufascore.domain.model.TeamLineup
import com.jerry.ronaldo.siufascore.presentation.ui.SiufascoreTheme
import com.jerry.ronaldo.siufascore.utils.FormationMapper
import com.jerry.ronaldo.siufascore.utils.getPositionAbbreviation

@Composable
fun LineUpScreen(
    teamLineup: TeamLineup,
    modifier: Modifier = Modifier,
    onPlayerClick: (Player) -> Unit = {},
    showBench: Boolean = false
) {
    val formationLayout = remember(teamLineup) {
        FormationMapper.mapToFormationLayout(teamLineup)
    }

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        // Đội hình chính với kích thước cố định
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f) // Tỷ lệ sân bóng thực tế
        ) {
            // Background sân bóng
            Image(
                painter = painterResource(R.drawable.line_up_bg),
                contentDescription = "Football Field",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // DEBUG: Hiển thị kích thước
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val fieldWidth = maxWidth
                val fieldHeight = maxHeight
                // Hiển thị tên đội hình
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = formationLayout.name,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                // Hiển thị các cầu thủ
                formationLayout.positions.forEachIndexed { _, playerPosition ->
                    val offsetX = fieldWidth * playerPosition.x - 27.5.dp
                    val offsetY = fieldHeight * playerPosition.y - 35.dp

                    EnhancedPlayerItem(
                        playerPosition = playerPosition,
                        modifier = Modifier.offset(x = offsetX, y = offsetY),
                        onPlayerClick = onPlayerClick
                    )
                }
            }
        }

       /* // Ghế dự bị
        if (showBench && formationLayout.bench.isNotEmpty()) {
            BenchSection(
                benchPlayers = formationLayout.bench,
                onPlayerClick = onPlayerClick
            )
        }*/
    }
}
@Composable
fun EnhancedPlayerItem(
    playerPosition: PlayerPosition,
    modifier: Modifier = Modifier,
    onPlayerClick: (Player) -> Unit = {}
) {
    val player = playerPosition.player

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clickable { onPlayerClick(player) }
    ) {
        // Avatar với số áo
        Box {
            AsyncImage(
                model = "https://img.a.transfermarkt.technology/portrait/big/${player.id}-${System.currentTimeMillis()}.jpg",
                contentDescription = player.name,
                modifier = Modifier
                    .size(if (playerPosition.isSubstitute) 35.dp else 45.dp)
                    .clip(CircleShape)
                    .border(
                        2.dp,
                        if (playerPosition.isSubstitute) Color.Yellow else Color.White,
                        CircleShape
                    )
                    .background(Color.Gray),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.premier_league),
                error = painterResource(R.drawable.premier_league)
            )

            // Số áo
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(if (playerPosition.isSubstitute) 16.dp else 18.dp),
                shape = CircleShape,
                color = Color.Blue,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = player.shirtNumber.toString(),
                        color = Color.White,
                        fontSize = 10.sp,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Tên cầu thủ
        Text(
            text = player.name.split(" ").lastOrNull() ?: player.name, // Chỉ lấy họ
            color = Color.White,
            fontSize = if (playerPosition.isSubstitute) 8.sp else 9.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(if (playerPosition.isSubstitute) 45.dp else 55.dp),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(1f, 1f),
                    blurRadius = 2f
                )
            )
        )

        // Vị trí
        if (!playerPosition.isSubstitute && player.position != null) {
            Text(
                text = player.position.getPositionAbbreviation(),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 7.sp,
                fontWeight = FontWeight.Medium,
                style = TextStyle(
                    shadow = Shadow(
                        color = Color.Black,
                        offset = Offset(0.5f, 0.5f),
                        blurRadius = 1f
                    )
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FootballLineupFromAPIPreview() {
    val sampleTeamLineup = TeamLineup(
        formation = "4-2-3-1",
        lineup = listOf(
            Player(3188, "David De Gea", "Goalkeeper", 1),
            Player(15905, "Diogo Dalot", "Right-Back", 20),
            Player(3360, "Raphaël Varane", "Centre-Back", 19),
            Player(3326, "Harry Maguire", "Centre-Back", 5),
            Player(7898, "Luke Shaw", "Left-Back", 23),
            Player(3366, "Paul Pogba", "Central Midfield", 6),
            Player(7905, "Scott McTominay", "Defensive Midfield", 39),
            Player(3257, "Bruno Fernandes", "Attacking Midfield", 18),
            Player(146, "Jadon Sancho", "Left Winger", 25),
            Player(44, "Cristiano Ronaldo", "Centre-Forward", 7),
            Player(3331, "Marcus Rashford", "Right Winger", 10)
        ),
        bench = listOf(
            Player(5457, "Dean Henderson", "Goalkeeper", 26),
            Player(3492, "Victor Nilsson-Lindelöf", "Centre-Back", 2),
            Player(7897, "Phil Jones", "Centre-Back", 4)
        )
    )

    SiufascoreTheme {
        LineUpScreen(
            teamLineup = sampleTeamLineup,
            modifier = Modifier.fillMaxSize(),
            showBench = true,
            onPlayerClick = { player ->
                println("Clicked on ${player.name}")
            }
        )
    }
}