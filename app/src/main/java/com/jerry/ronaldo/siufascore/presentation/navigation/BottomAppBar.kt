package com.jerry.ronaldo.siufascore.presentation.navigation

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BottomAppBar(
    modifier: Modifier = Modifier,
    bottomBarHeight: Dp = 64.dp,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    containerShape: Shape = RectangleShape,
    selectedItem: Int? = null,
    itemSize: Int? = null,
    indicatorColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    indicatorHeight: Dp = 4.dp,
    animationSpec: AnimationSpec<Dp> =
        spring(
            dampingRatio = 1f,
            stiffness = Spring.StiffnessMediumLow,
        ),
    indicatorShape: RoundedCornerShape = RoundedCornerShape(25.dp),
    content: @Composable RowScope.() -> Unit,
) {
    Surface(
        color = containerColor,
        contentColor = contentColor,
        modifier = modifier,
        shape = containerShape,
    ) {
        // Use Box instead of separate BoxWithConstraints and Row
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(bottomBarHeight)
        ) {
            // Content Row
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .selectableGroup(),
                horizontalArrangement = Arrangement.SpaceEvenly, // Changed from SpaceBetween
                verticalAlignment = Alignment.CenterVertically,
                content = content
            )

            // Line Indicator on top
            selectedItem?.let { selected ->
                itemSize?.let { size ->
                    BoxWithConstraints(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                    ) {
                        val maxWidth = this.maxWidth
                        val itemWidth = maxWidth / size
                        val indicatorOffset: Dp by animateDpAsState(
                            targetValue = itemWidth * selected,
                            animationSpec = animationSpec,
                            label = "Indicator"
                        )

                        LineIndicator(
                            indicatorOffset = indicatorOffset,
                            itemWidth = itemWidth,
                            indicatorColor = indicatorColor,
                            indicatorHeight = indicatorHeight,
                            indicatorShape = indicatorShape,
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun LineIndicator(
    indicatorOffset: Dp,
    itemWidth: Dp,
    indicatorColor: Color,
    indicatorHeight: Dp,
    indicatorShape: RoundedCornerShape,
) {
    Box(
        modifier = Modifier
            .offset(x = indicatorOffset)
            .width(itemWidth)
            .height(indicatorHeight)
            .padding(horizontal = 16.dp) // Add some padding so indicator is not full width
            .clip(shape = indicatorShape)
            .background(indicatorColor, shape = indicatorShape),
    )
}