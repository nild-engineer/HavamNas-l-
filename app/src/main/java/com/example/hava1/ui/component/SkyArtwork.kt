package com.example.hava1.ui.component

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import kotlin.math.cos
import kotlin.math.sin

/** Dekoratif marka çizimi; herhangi bir şehrin canlı havasını temsil etmez. */
@Composable
fun SkyArtwork(modifier: Modifier = Modifier, animated: Boolean = true) {
    val movement =
        if (animated) {
            val transition = rememberInfiniteTransition(label = "skyArtwork")
            val offset by
                transition.animateFloat(
                    0f,
                    5f,
                    infiniteRepeatable(
                        tween(4000, easing = FastOutSlowInEasing),
                        RepeatMode.Reverse,
                    ),
                    label = "cloudDrift",
                )
            offset
        } else 0f
    Canvas(modifier) {
        val scale = size.width / 360f
        val origin = Offset(size.width / 2f, size.height / 2f)
        with(drawContext.canvas) {
            save()
            translate(origin.x - 180f * scale, origin.y - 90f * scale)
            scale(scale, scale)
            drawCircle(
                Brush.radialGradient(
                    listOf(Color(0xFFE8F6FF), Color.Transparent),
                    Offset(215f, 85f),
                    170f,
                ),
                170f,
                Offset(215f, 85f),
            )
            drawOval(Color(0xFFDDECF5).copy(alpha = .65f), Offset(82f, 151f), Size(218f, 18f))
            val sun = Offset(230f, 65f)
            drawCircle(Color(0xFFFFE2A0).copy(alpha = .35f), 64f, sun)
            for (i in 0..11) {
                val angle = i * Math.PI / 6
                drawLine(
                    Color(0xFFEFB946),
                    sun + Offset((cos(angle) * 49).toFloat(), (sin(angle) * 49).toFloat()),
                    sun + Offset((cos(angle) * 55).toFloat(), (sin(angle) * 55).toFloat()),
                    2.7f,
                    StrokeCap.Round,
                )
            }
            drawCircle(
                Brush.linearGradient(
                    listOf(Color(0xFFFFDD83), Color(0xFFF3AD45)),
                    Offset(205f, 35f),
                    Offset(257f, 96f),
                ),
                38f,
                sun,
            )
            val dx = if (animated) movement else 0f
            translate(dx, 0f)
            val cloud =
                Path().apply {
                    moveTo(97f, 139f)
                    cubicTo(60f, 139f, 60f, 99f, 94f, 96f)
                    cubicTo(96f, 59f, 148f, 49f, 168f, 81f)
                    cubicTo(192f, 61f, 224f, 80f, 222f, 105f)
                    cubicTo(261f, 105f, 265f, 143f, 225f, 144f)
                    lineTo(97f, 144f)
                    close()
                }
            translate(0f, 5f)
            drawPath(cloud, Color(0xFFBDDBEC).copy(alpha = .55f))
            translate(0f, -5f)
            drawPath(
                cloud,
                Brush.linearGradient(
                    listOf(Color.White, Color(0xFFEAF4FB)),
                    Offset(140f, 70f),
                    Offset(140f, 150f),
                ),
            )
            drawLine(Color.White, Offset(105f, 103f), Offset(116f, 91f), 3f, StrokeCap.Round)
            translate(-dx, 0f)
            drawCircle(Color(0xFFAECEDA), 3f, Offset(58f, 74f))
            drawCircle(Color(0xFFAECEDA), 2f, Offset(310f, 114f))
            drawLine(Color(0xFF97BECE), Offset(292f, 43f), Offset(292f, 53f), 2f, StrokeCap.Round)
            drawLine(Color(0xFF97BECE), Offset(287f, 48f), Offset(297f, 48f), 2f, StrokeCap.Round)
            restore()
        }
    }
}

@Composable
fun HomeLineIcon(kind: String, modifier: Modifier = Modifier, color: Color = Color(0xFF2F617C)) {
    Canvas(modifier) {
        val u = size.width / 24f
        fun line(a: Float, b: Float, c: Float, d: Float) =
            drawLine(color, Offset(a * u, b * u), Offset(c * u, d * u), 1.7f * u, StrokeCap.Round)
        when (kind) {
            "search" -> {
                drawCircle(
                    color,
                    6.8f * u,
                    Offset(10.3f * u, 10.3f * u),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.7f * u),
                )
                line(15.4f, 15.4f, 21f, 21f)
            }
            "close" -> {
                line(7f, 7f, 17f, 17f)
                line(17f, 7f, 7f, 17f)
            }
            "arrow" -> {
                line(5f, 12f, 19f, 12f)
                line(14f, 7f, 19f, 12f)
                line(14f, 17f, 19f, 12f)
            }
            "calendar" -> {
                drawRoundRect(
                    color,
                    Offset(4f * u, 5f * u),
                    Size(16f * u, 15f * u),
                    androidx.compose.ui.geometry.CornerRadius(3f * u),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.6f * u),
                )
                line(4f, 10f, 20f, 10f)
                line(8f, 3f, 8f, 7f)
                line(16f, 3f, 16f, 7f)
                drawCircle(color, 1f * u, Offset(9f * u, 14f * u))
                drawCircle(color, 1f * u, Offset(15f * u, 14f * u))
            }
            "bag" -> {
                drawRoundRect(
                    color,
                    Offset(5f * u, 8f * u),
                    Size(14f * u, 13f * u),
                    androidx.compose.ui.geometry.CornerRadius(3f * u),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.6f * u),
                )
                drawArc(
                    color,
                    180f,
                    180f,
                    false,
                    Offset(8f * u, 3f * u),
                    Size(8f * u, 10f * u),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.6f * u),
                )
            }
            else -> {
                drawCircle(
                    color,
                    7f * u,
                    Offset(12f * u, 12f * u),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(1.6f * u),
                )
                line(12f, 8f, 12f, 12f)
                line(12f, 12f, 15f, 14f)
            }
        }
    }
}
