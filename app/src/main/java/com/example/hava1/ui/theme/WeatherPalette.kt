package com.example.hava1.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.hava1.model.CurrentWeather

data class WeatherPalette(
    val top: Color,
    val bottom: Color,
    val accent: Color,
    val symbol: String,
    val caption: String,
)

fun weatherPalette(current: CurrentWeather?): WeatherPalette {
    val code = current?.weatherCode
    val night = current?.isDay == false
    if (night) {
        val top =
            when (code) {
                45,
                48 -> Color(0xFF293A3E)
                71,
                73,
                75,
                77,
                85,
                86 -> Color(0xFF24394F)
                95,
                96,
                99 -> Color(0xFF352841)
                51,
                53,
                55,
                56,
                57,
                61,
                63,
                65,
                66,
                67,
                80,
                81,
                82 -> Color(0xFF163646)
                else -> Color(0xFF18243D)
            }
        return WeatherPalette(
            top,
            Color(0xFF33435F),
            Color(0xFFDEE9FF),
            when (code) {
                0,
                1 -> "🌙"
                45,
                48 -> "🌫️"
                95,
                96,
                99 -> "⛈️"
                71,
                73,
                75,
                77,
                85,
                86 -> "🌨️"
                51,
                53,
                55,
                56,
                57,
                61,
                63,
                65,
                66,
                67,
                80,
                81,
                82 -> "🌧️"
                else -> "☁️"
            },
            "Gecenin Havası",
        )
    }
    return when (code) {
        0,
        1 ->
            WeatherPalette(
                Color(0xFFFFE4AB),
                Color(0xFFFFF7E5),
                Color(0xFF6E4300),
                "☀️",
                "Gökyüzüne Bir Bak",
            )
        2,
        3 ->
            WeatherPalette(
                Color(0xFFC9DBEC),
                Color(0xFFF0F5F9),
                Color(0xFF284B68),
                "☁️",
                "Bulutların Arasında",
            )
        45,
        48 ->
            WeatherPalette(
                Color(0xFFD4DEDC),
                Color(0xFFF0F4F2),
                Color(0xFF3B5750),
                "🌫️",
                "Puslu Bir Gün",
            )
        51,
        53,
        55,
        56,
        57,
        61,
        63,
        65,
        66,
        67,
        80,
        81,
        82 ->
            WeatherPalette(
                Color(0xFFB6D7E4),
                Color(0xFFE9F4F7),
                Color(0xFF14516A),
                "🌧️",
                "Yağmurun Ritmi",
            )
        71,
        73,
        75,
        77,
        85,
        86 ->
            WeatherPalette(
                Color(0xFFDAE9F5),
                Color(0xFFF6FAFF),
                Color(0xFF365877),
                "❄️",
                "Bembeyaz Bir Hava",
            )
        95,
        96,
        99 ->
            WeatherPalette(
                Color(0xFFD2C9E3),
                Color(0xFFF2EDF8),
                Color(0xFF513969),
                "⛈️",
                "Gökyüzü Hareketli",
            )
        else ->
            WeatherPalette(
                Color(0xFFCBEBE2),
                Color(0xFFF2F8F5),
                Color(0xFF245E50),
                "🌤️",
                "Günün Havası",
            )
    }
}
