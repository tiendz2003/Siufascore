package com.jerry.ronaldo.siufascore.presentation.favorite

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.data.model.FavoritePlayer
import com.jerry.ronaldo.siufascore.presentation.ui.EmptyScreen
import timber.log.Timber

@Composable
fun FavoritePlayersScreen(
    modifier: Modifier = Modifier,
    uiState: FavoriteUiState,
    onPlayerClick: (FavoritePlayer) -> Unit = {},
) {
    Timber.tag("FavoritePlayersScreen").d("FavoritePlayers: ${uiState.favoritePlayers}")
    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isPlayerLoading -> {
                Loading()
            }

            uiState.favoritePlayers.isEmpty() -> {
                EmptyScreen()
            }

            else -> {
                FavoritePlayersContent(
                    players = uiState.favoritePlayers,
                    onPlayerClick = onPlayerClick,
                    onToggleNotification = { playerId, isEnabled ->
                        //viewModel.togglePlayerNotification(playerId, isEnabled)
                    },
                    onRemovePlayer = { playerId ->
                        //viewModel.removeFavoritePlayer(playerId)
                    }
                )
            }
        }
    }
}

@Composable
private fun FavoritePlayersContent(
    players: List<FavoritePlayer>,
    onPlayerClick: (FavoritePlayer) -> Unit,
    onToggleNotification: (String, Boolean) -> Unit,
    onRemovePlayer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "Danh sách Cầu thủ Yêu thích",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = players,
            key = { it.playerId }
        ) { player ->
            FavoritePlayerCard(
                player = player,
                onClick = { onPlayerClick(player) },
                onToggleNotification = { isEnabled ->
                    onToggleNotification(player.playerId, isEnabled)
                },
                onRemove = { onRemovePlayer(player.playerId) },
                modifier = Modifier.animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp)) // Bottom padding for FAB
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritePlayerCard(
    player: FavoritePlayer,
    onClick: () -> Unit,
    onToggleNotification: (Boolean) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal =  16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Player Photo
                AsyncImage(
                    model =
                        player.playerPhoto.ifEmpty { R.drawable.premier_league },
                    contentDescription = "Player Photo",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        .padding(1.dp),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Player Info
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = player.playerName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "Vị trí: ${player.playerPosition}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Quốc gia: ${player.playerNationality}",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Light,
                            fontSize = MaterialTheme.typography.bodyMedium.fontSize
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Action Buttons
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Notification Toggle
                    IconButton(
                        onClick = { onToggleNotification(!player.enableNotification) }
                    ) {
                        Icon(
                            imageVector = if (player.enableNotification) {
                                Icons.Filled.Notifications
                            } else {
                                Icons.Outlined.NotificationsOff
                            },
                            contentDescription = if (player.enableNotification) {
                                "Disable notifications"
                            } else {
                                "Enable notifications"
                            },
                            tint = if (player.enableNotification) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }

                    // Remove Button
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = "Remove from favorites",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Xóa Cầu thủ Yêu thích", style =

                        MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.error
                        )
                )
            },
            text = {
                Text("Bạn chắc chắn muốn xóa ${player.playerName} khỏi danh sách yêu thích")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onRemove()
                        showDeleteDialog = false
                    }
                ) {
                    Text(
                        "Xóa",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ), color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text(
                        "Cancel",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }
}
