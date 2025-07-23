package com.jerry.ronaldo.siufascore.presentation.favorite

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.jerry.ronaldo.siufascore.data.model.FavoriteTeam
import com.jerry.ronaldo.siufascore.presentation.ui.PremierPurpleDark
import com.jerry.ronaldo.siufascore.presentation.ui.Purple

@Composable
fun FavoriteTeamScreen(
    uiState: FavoriteUiState,
    onLeagueSelected: (AvailableLeague) -> Unit,
    onRemoveTeam: (Int) -> Unit,
    onNotificationToggle: (Int) -> Unit,
    onNavigateToTeamDetail: (Int, Int) -> Unit = { _, _ -> },
) {
    if (uiState.isEmpty) {
        EmptyFavoriteTeamsContent(
            modifier = Modifier.fillMaxSize()
        )
    } else {
        LeagueTabsRow(
            uiState = uiState,
            onLeagueSelected = { league ->
                onLeagueSelected(league)
            },
            onNotificationToggle = {teamId->
                onNotificationToggle(teamId)
            },
            onRemoveTeam = {
                onRemoveTeam(it)
            },
            onNavigateToTeamDetail = { teamId, leagueId ->
                onNavigateToTeamDetail(teamId, leagueId)
            }
        )
    }
}

@Composable
private fun LeagueTabsRow(
    modifier: Modifier = Modifier,
    uiState: FavoriteUiState,
    onLeagueSelected: (AvailableLeague) -> Unit,
    onRemoveTeam: (Int) -> Unit,
    onNotificationToggle: (Int) -> Unit,
    onNavigateToTeamDetail: (Int, Int) -> Unit = { _, _ -> },
) {
    val leagues = listOf(
        AvailableLeague.PREMIER_LEAGUE,
        AvailableLeague.LA_LIGA,
        AvailableLeague.BUNDESLIGA,
        AvailableLeague.SERIE_A,
        AvailableLeague.LIGUE_1,
    )

    val selectedTab = leagues.indexOf(uiState.selectedLeagueType).coerceAtLeast(0)
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    ScrollableTabRow(
        selectedTabIndex = selectedTab,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        indicator = { tabPositions ->
            if (tabPositions.isNotEmpty() && selectedTab < tabPositions.size) {
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        leagues.forEachIndexed { index, league ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = {
                    selectedTabIndex = index
                    onLeagueSelected(league)
                },
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = league.displayName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 14.sp
                    ),
                    fontWeight = if (index == selectedTab) FontWeight.Medium else FontWeight.Normal,
                    modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
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
    ) { index ->
        when (leagues[index]) {
            AvailableLeague.PREMIER_LEAGUE -> {
                FavoriteTeamsList(
                    teams = uiState.currentTeams,
                    isTogglingNotification = uiState.isTogglingNotification,
                    toggledTeamId = uiState.toggledTeamId,
                    onNotificationToggle = { teamId ->
                        onNotificationToggle(teamId)
                    },
                    onTeamClick = {
                        onNavigateToTeamDetail(it.team.id, it.league.id)
                    },
                    onRemoveTeam = { teamId ->
                        onRemoveTeam(teamId)
                    }
                )
            }

            else -> {
                FavoriteTeamsList(
                    teams = uiState.currentTeams,
                    isTogglingNotification = uiState.isTogglingNotification,
                    toggledTeamId = uiState.toggledTeamId,
                    onNotificationToggle = { teamId ->
                        onNotificationToggle(teamId)
                    },
                    onTeamClick = {
                        onNavigateToTeamDetail(it.team.id, it.league.id)
                    },
                    onRemoveTeam = { teamId ->
                        onRemoveTeam(teamId)
                    }
                )
            }
        }
    }
}

@Composable
private fun FavoriteTeamsList(
    teams: List<FavoriteTeam>,
    isTogglingNotification: Boolean,
    toggledTeamId: Int?,
    onTeamClick: (FavoriteTeam) -> Unit,
    onNotificationToggle: (Int) -> Unit,
    onRemoveTeam: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (teams.isEmpty()) {
        EmptyLeagueContent(
            modifier = modifier.fillMaxSize()
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = teams,
            key = { it.team.id }
        ) { team ->
            FavoriteTeamItem(
                team = team,
                isTogglingNotification = isTogglingNotification && toggledTeamId == team.team.id,
                onTeamClick = { onTeamClick(team) },
                onNotificationToggle = { onNotificationToggle(team.team.id) },
                onRemoveTeam = { onRemoveTeam(team.team.id) },
                modifier = Modifier.animateItem(
                    fadeInSpec = tween(durationMillis = 500),
                    fadeOutSpec = tween(durationMillis = 500)
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteTeamItem(
    team: FavoriteTeam,
    isTogglingNotification: Boolean,
    onTeamClick: () -> Unit,
    onNotificationToggle: () -> Unit,
    onRemoveTeam: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        onClick = onTeamClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = BorderStroke(
            color = PremierPurpleDark,
            width = 1.dp
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Team Logo
            AsyncImage(
                model = team.team.logo,
                contentDescription = "${team.team.name} logo",
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape).padding(4.dp),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.premier_league),
                error = painterResource(R.drawable.premier_league)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Team Info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = team.team.name,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 18.sp
                    ),
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country flag or icon
                    team.league.logo?.let { flagUrl ->
                        AsyncImage(
                            model = flagUrl,
                            contentDescription = "${team.league.country} flag",
                            modifier = Modifier
                                .size(18.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            placeholder = painterResource(R.drawable.premier_league),
                            error = painterResource(R.drawable.premier_league),
                            contentScale = ContentScale.Fit
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Text(
                        text = team.league.country ?: team.league.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Notification Toggle Button
                IconButton(
                    onClick = onNotificationToggle,
                    enabled = !isTogglingNotification
                ) {
                    if (isTogglingNotification) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    } else {
                        Icon(
                            imageVector = if (team.enableNotification) {
                                Icons.Default.NotificationsActive
                            } else {
                                Icons.Default.NotificationsOff
                            },
                            contentDescription = if (team.enableNotification) {
                                "Disable notifications"
                            } else {
                                "Enable notifications"
                            },
                            tint = if (team.enableNotification) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                }

                // Remove Button
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from favorites",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Xóa khỏi đội bóng yêu thích",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = 14.sp
                    )
                )
            },
            text = {
                Text("Bạn muốn xóa ${team.team.name} khỏi danh sách yêu thích")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemoveTeam()
                        showDeleteDialog = false
                    }
                ) {
                    Text("Xóa")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Thoát")
                }
            }
        )
    }
}

@Composable
private fun EmptyFavoriteTeamsContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = Purple
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chưa có đội bóng yêu thích nào",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hãy thêm đội bóng yêu thích của bạn để nhận thông báo và cập nhật mới nhất",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun EmptyLeagueContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SportsSoccer,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = Purple
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Không có đội bóng yêu thích nào",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
    }
}

