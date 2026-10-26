package com.example.hava1.ui.city

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun CitySearchShimmer(
    modifier: Modifier = Modifier,
) {
    val transition =
        rememberInfiniteTransition(
            label = "shimmer",
        )

    val position =
        transition.animateFloat(
            initialValue = -600f,
            targetValue = 1200f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        tween(
                            durationMillis = 1200,
                            easing = LinearEasing,
                        ),
                    repeatMode = RepeatMode.Restart,
                ),
            label = "shimmerPosition",
        )

    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val highlightColor = MaterialTheme.colorScheme.surface

    val brush =
        Brush.linearGradient(
            colors =
                listOf(
                    baseColor,
                    highlightColor,
                    baseColor,
                ),
            start = Offset(position.value, 0f),
            end = Offset(position.value + 500f, 300f),
        )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        repeat(3) {
            Box(
                modifier =
                    Modifier.fillMaxWidth()
                        .height(88.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(brush),
            )
        }
    }
}
