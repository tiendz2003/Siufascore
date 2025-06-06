package com.jerry.ronaldo.siufascore.presentation.matches.screen.item

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.jerry.ronaldo.siufascore.R
import com.jerry.ronaldo.siufascore.presentation.matches.screen.CompetitionSelector
import com.jerry.ronaldo.siufascore.presentation.ui.Purple

@Composable
fun CompetitionSelectorItem(
    competition: CompetitionSelector,
    isSelected: Boolean,
    onClick: () -> Unit,
    animationDelay: Int
) {
    var hasAnimated by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )
    val backgroundColor by animateColorAsState(
        targetValue = Color.White,
        animationSpec = tween(300),
        label = "backgroundColor"
    )
    val contentColor by animateColorAsState(
        targetValue = if (isSelected) Purple else Color.White,
        animationSpec = tween(300),
        label = "contentColor"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Purple else Color.LightGray,
        animationSpec = tween(300),
        label = "borderColor"
    )
    val offsetX by animateIntAsState(
        targetValue = 0,
        animationSpec = if (!hasAnimated) {
            tween(600, animationDelay, FastOutSlowInEasing)
        } else {
            spring()
        },
        label = "offsetX"
    )
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = if (!hasAnimated) {
            tween(600, animationDelay, FastOutSlowInEasing)
        } else {
            tween(300)
        },
        label = "alpha"
    )
    LaunchedEffect(Unit) {
        hasAnimated = true
    }
    Card(
        modifier = Modifier
            .alpha(alpha)
            .scale(scale)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(
                    bounded = true,
                    radius = 40.dp,
                    color = Purple
                ),
                onClick = onClick
            )
            .offset {
                IntOffset(offsetX, 0)
            },
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = if (isSelected) 8.dp else 0.dp
        ),

        ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .animateContentSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (isSelected) Arrangement.spacedBy(8.dp) else Arrangement.spacedBy(
                0.dp
            ),
        ) {
            CompetitionLogo(
                logoUrl = competition.logo,
                isSelected = isSelected,
                modifier = Modifier.size(32.dp)
            )
            AnimatedContent(
                targetState = isSelected,
                transitionSpec = {
                    slideInHorizontally(
                        initialOffsetX = { width ->
                            width
                        },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(
                        animationSpec = tween(300, 100)
                    ) togetherWith
                            slideOutHorizontally(
                                targetOffsetX = { width ->
                                    -width
                                },
                                animationSpec = tween(200, easing = FastOutLinearInEasing)
                            ) + fadeOut(
                        tween(150)
                    )
                },
                label = "competitionName"
            ) { targetIsSelected ->
                AnimatedVisibility(
                    visible = targetIsSelected,
                    enter = slideInHorizontally(
                        initialOffsetX = { width ->
                            width
                        },
                        animationSpec = tween(400, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(400)),
                    exit = slideOutHorizontally(
                        targetOffsetX = { width ->
                            width
                        },
                        animationSpec = tween(200, easing = FastOutLinearInEasing)
                    ) + fadeOut(tween(150))
                ) {
                    Text(
                        text = competition.name,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        ),
                        fontSize = 14.sp,
                        color = contentColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        )
                }
            }
        }
    }
}

@Composable
private fun CompetitionLogo(
    @DrawableRes logoUrl: Int,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val logoScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "logoScale"
    )
    val rotation by animateFloatAsState(
        targetValue = if (isSelected) 360f else 0f,
        animationSpec = tween(durationMillis = 500),
        label = "rotation"
    )
    AsyncImage(
        model = logoUrl,
        contentDescription = "Competition Logo",
        modifier = modifier
            .scale(logoScale)
            .rotate(rotation),
        placeholder = painterResource(R.drawable.ic_launcher_foreground),
        error = painterResource(R.drawable.ic_launcher_foreground)
    )
}