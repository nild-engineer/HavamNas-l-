package com.example.hava1.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.hava1.R

val HavaFont =
    FontFamily(
        Font(R.font.manrope_regular, FontWeight.Normal),
        Font(R.font.manrope_medium, FontWeight.Medium),
        Font(R.font.manrope_semibold, FontWeight.SemiBold),
        Font(R.font.manrope_bold, FontWeight.Bold),
    )

private fun text(size: Int, height: Int, weight: FontWeight = FontWeight.Normal) =
    TextStyle(
        fontFamily = HavaFont,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = height.sp,
        letterSpacing = 0.sp,
    )

val Typography =
    Typography(
        displayLarge = text(52, 60),
        displayMedium = text(44, 52),
        displaySmall = text(36, 44),
        headlineLarge = text(30, 38, FontWeight.Bold),
        headlineMedium = text(26, 34, FontWeight.Bold),
        headlineSmall = text(23, 31, FontWeight.SemiBold),
        titleLarge = text(21, 29, FontWeight.SemiBold),
        titleMedium = text(17, 25, FontWeight.SemiBold),
        titleSmall = text(15, 23, FontWeight.SemiBold),
        bodyLarge = text(16, 25),
        bodyMedium = text(14, 22),
        bodySmall = text(12, 19),
        labelLarge = text(14, 21, FontWeight.SemiBold),
        labelMedium = text(12, 18, FontWeight.Medium),
        labelSmall = text(11, 17, FontWeight.Medium),
    )
