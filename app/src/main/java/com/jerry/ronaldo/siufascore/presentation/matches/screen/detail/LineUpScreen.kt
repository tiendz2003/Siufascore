package com.jerry.ronaldo.siufascore.presentation.matches.screen.detail

import com.jerry.ronaldo.siufascore.utils.FormationMapper
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.domain.model.PlayerMatchStats
import com.jerry.ronaldo.siufascore.domain.model.PlayerPosition
import com.jerry.ronaldo.siufascore.utils.getPositionAbbreviation
import timber.log.Timber

@Composable
fun LineUpScreen(
    homeFormation: String,
    awayFormation: String,
    homeLineup: List<PlayerMatchStats>,
    awayLineup: List<PlayerMatchStats>,
    modifier: Modifier = Modifier,
    onPlayerClick: (PlayerMatchStats) -> Unit = {},
    showBench: Boolean = false
) {
    // State for selected tab
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Home Team", "Away Team")

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        // TabRow with modern styling
        ScrollableTabRow(
            modifier = Modifier
                .wrapContentWidth()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    modifier = Modifier
                        .tabIndicatorOffset(tabPositions[selectedTabIndex])
                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)),
                    height = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .height(48.dp),
                    selectedContentColor = MaterialTheme.colorScheme.primary,
                    unselectedContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        }

        // Animated content transition
        AnimatedContent(
            targetState = selectedTabIndex,
            transitionSpec = {
                if (targetState > initialState) {
                    (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> -width } + fadeOut())
                } else {
                    (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                        slideOutHorizontally { width -> width } + fadeOut())
                }.using(SizeTransform(clip = false))
            }
        ) { tabIndex ->
            when (tabIndex) {
                0 -> TeamLineupContent(
                    formation = homeFormation,
                    teamLineup = homeLineup,
                    modifier = Modifier.fillMaxSize(),
                    onPlayerClick = onPlayerClick,
                    showBench = showBench
                )

                1 -> TeamLineupContent(
                    formation = awayFormation,
                    teamLineup = awayLineup,
                    modifier = Modifier.fillMaxSize(),
                    onPlayerClick = onPlayerClick,
                    showBench = showBench
                )
            }
        }
    }
}

@Composable
private fun TeamLineupContent(
    formation: String,
    teamLineup: List<PlayerMatchStats>,
    modifier: Modifier = Modifier,
    onPlayerClick: (PlayerMatchStats) -> Unit,
    showBench: Boolean
) {
    val formationLayout = remember(teamLineup, formation) {
        FormationMapper.mapToFormationLayout(
            formation = formation,
            lineUp = teamLineup
        )
    }

    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.7f)
        ) {
            Image(
                painter = painterResource(R.drawable.line_up_bg),
                contentDescription = "Football Field",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                val fieldWidth = maxWidth
                val fieldHeight = maxHeight

                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.Black.copy(alpha = 0.7f)
                ) {
                    Text(
                        text = formation,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }

                formationLayout.positions.forEachIndexed { _, playerPosition ->
                    val offsetX = fieldWidth * playerPosition.x - 27.5.dp
                    val offsetY = fieldHeight * playerPosition.y
                    Timber.tag("LineUpScreen").d("PlayerPosition: $playerPosition")
                    EnhancedPlayerItem(
                        playerPosition = playerPosition,
                        modifier = Modifier.offset(x = offsetX, y = offsetY),
                        onPlayerClick = onPlayerClick
                    )
                }
            }
        }
    }
}

@Composable
fun EnhancedPlayerItem(
    playerPosition: PlayerPosition,
    modifier: Modifier = Modifier,
    onPlayerClick: (PlayerMatchStats) -> Unit = {}
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
                model = player.player.photo,
                contentDescription = player.player.name,
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
            text = player.player.name.split(" ").lastOrNull() ?: player.player.name, // Chỉ lấy họ
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
