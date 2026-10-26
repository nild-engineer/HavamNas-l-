package com.example.hava1.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = NightSkyBlue,
        secondary = NightSlate,
        tertiary = NightWarmAccent,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = SkyBlue,
        secondary = SlateBlue,
        tertiary = WarmAccent,
    )

@Composable
fun Hava1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // İstenirse Android 12+ cihazlarda sistem renkleri kullanılabilir.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
