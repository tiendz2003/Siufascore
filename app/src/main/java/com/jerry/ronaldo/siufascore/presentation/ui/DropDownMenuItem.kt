package com.jerry.ronaldo.siufascore.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jerry.ronaldo.siufascore.utils.League

@Composable
fun ColumnScope.LeagueDropdownItem(
    league: League,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box {
                    Image(
                        imageVector = ImageVector.vectorResource(id = league.iconRes),
                        contentDescription = league.name,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                    CircleShape
                                )
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = league.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }
                    )
                    Text(
                        text = league.country,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                } else {
                    Color.Transparent
                }
            ),
        contentPadding = PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        )
    )
}