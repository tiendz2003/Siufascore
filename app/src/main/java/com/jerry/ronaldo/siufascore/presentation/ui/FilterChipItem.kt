package com.jerry.ronaldo.siufascore.presentation.ui

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jerry.ronaldo.siufascore.utils.Chip

@Composable
fun FilterChipItem(
    options: List<String>,
    selectedOption: String,
    onSelectedOptionChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(options) { option ->
            FilterChip(
                selected = option == selectedOption,
                onClick = {
                    onSelectedOptionChange(option)
                },
                label = {
                    Text(
                        text = option,
                        style = MaterialTheme.typography.labelSmall.copy(
                            if (option == selectedOption) Color.White else Purple
                        ),
                        fontWeight = if (option == selectedOption) FontWeight.SemiBold else FontWeight.Normal
                    )
                },
                modifier = Modifier.animateContentSize(),
                leadingIcon = if (option == selectedOption) {
                    {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                } else null,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Purple,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    containerColor = MaterialTheme.colorScheme.surface,
                    labelColor = MaterialTheme.colorScheme.onSurface
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = option == selectedOption,
                    selectedBorderColor = MaterialTheme.colorScheme.primary,
                    borderColor = Purple
                ),
            )
        }
    }
}

@Composable
fun <T> TypeFilterChips(
    items:List<T>,
    selectedType: T,
    onTypeSelected: (T) -> Unit,
    icon:(T) -> ImageVector,
    modifier: Modifier = Modifier
) where T: Chip {
    LazyRow(
        modifier = modifier.padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(items) { item ->
            FilterChip(
                selected = selectedType == item,
                onClick = { onTypeSelected(item) },
                label = {
                    Text(
                        text = item.displayName,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = icon(item),
                        contentDescription = null
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Purple,
                    selectedLabelColor = Color.White,
                    selectedLeadingIconColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}