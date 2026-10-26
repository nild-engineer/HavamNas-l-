package com.example.hava1.model

enum class WeatherCondition(
    val emoji: String,
    val label: String,
) {
    CLEAR("☀️", "Açık"),
    MAINLY_CLEAR("🌤️", "Az bulutlu"),
    PARTLY_CLOUDY("⛅", "Parçalı bulutlu"),
    OVERCAST("☁️", "Kapalı"),
    FOG("🌫️", "Sisli"),
    DRIZZLE("🌦️", "Çisentili"),
    FREEZING_DRIZZLE("🧊", "Donan çisenti"),
    RAIN("🌧️", "Yağmurlu"),
    FREEZING_RAIN("🧊", "Donan yağmur"),
    SNOW("🌨️", "Karlı"),
    SNOW_GRAINS("🌨️", "Kar taneleri"),
    RAIN_SHOWERS("🌦️", "Sağanak yağışlı"),
    SNOW_SHOWERS("🌨️", "Kar sağanağı"),
    THUNDERSTORM("⛈️", "Gök gürültülü fırtına"),
    THUNDERSTORM_HAIL("⛈️", "Dolu ile gök gürültülü fırtına"),
    UNKNOWN("❔", "Hava durumu bilgisi yok");

    companion object {
        fun fromCode(code: Int?): WeatherCondition {
            return when (code) {
                0 -> CLEAR
                1 -> MAINLY_CLEAR
                2 -> PARTLY_CLOUDY
                3 -> OVERCAST
                45,
                48 -> FOG
                51,
                53,
                55 -> DRIZZLE
                56,
                57 -> FREEZING_DRIZZLE
                61,
                63,
                65 -> RAIN
                66,
                67 -> FREEZING_RAIN
                71,
                73,
                75 -> SNOW
                77 -> SNOW_GRAINS
                80,
                81,
                82 -> RAIN_SHOWERS
                85,
                86 -> SNOW_SHOWERS
                95 -> THUNDERSTORM
                96,
                99 -> THUNDERSTORM_HAIL
                else -> UNKNOWN
            }
        }
    }
}
