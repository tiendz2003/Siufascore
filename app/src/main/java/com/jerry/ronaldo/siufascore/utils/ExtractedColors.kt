package com.jerry.ronaldo.siufascore.utils

import androidx.compose.ui.graphics.Color
import com.jerry.ronaldo.siufascore.presentation.ui.PremierPurpleDark

data class ExtractedColors(
    val dominant: Color = PremierPurpleDark,
    val vibrant: Color = PremierPurpleDark,
    val muted: Color = PremierPurpleDark,
    val lightVibrant: Color = PremierPurpleDark,
    val onVibrant: Color = Color.White
)
