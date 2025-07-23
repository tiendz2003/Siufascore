package com.jerry.ronaldo.siufascore.presentation.search

import androidx.annotation.Keep
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector
import com.jerry.ronaldo.siufascore.utils.Chip

@Keep
enum class SearchType(override val displayName:String, val icon:ImageVector):Chip {
    TEAMS("Câu lạc bộ", Icons.Default.Groups),
    PLAYERS("Cầu thủ",Icons.Default.Person);
    companion object {
        val DEFAULT = TEAMS
    }
}
