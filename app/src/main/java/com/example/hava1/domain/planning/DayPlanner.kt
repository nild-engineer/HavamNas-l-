package com.example.hava1.domain.planning

import com.example.hava1.model.*

enum class PlanLevel(val number: Int, val label: String) {
    OUTDOOR(1, "Açık hava planına uygun"),
    PREPARED(2, "Hazırlıklı çıkılabilir"),
    INDOOR(3, "Kapalı mekân tercih et"),
    SEVERE(4, "Dışarı planı önermeyiz"),
}

data class DayAssessment(val level: PlanLevel?, val reasons: List<String>)

/** Günlük planlama için ürün kuralları; resmî meteorolojik uyarı değildir. */
object DayPlanner {
    fun assess(day: DailyWeather): DayAssessment {
        val severe = mutableListOf<String>()
        val indoor = mutableListOf<String>()
        val prepared = mutableListOf<String>()
        val code = day.weatherCode
        val wind = day.maxWindSpeed?.takeIf { it.isFinite() && it >= 0 }
        val gust = day.maxWindGust?.takeIf { it.isFinite() && it >= 0 }
        val rain = day.precipitationSum?.takeIf { it.isFinite() && it >= 0 }
        val chance = day.rainProbability
        val high = day.maxTemperature?.takeIf { it.isFinite() }
        val low = day.minTemperature?.takeIf { it.isFinite() }
        fun number(value: Double) =
            String.format(java.util.Locale.forLanguageTag("tr-TR"), "%.1f", value)
        if (code in setOf(95, 96, 99)) severe += "Gök gürültülü fırtına tahmini var."
        if (wind != null && wind >= 60) severe += "Günün en yüksek rüzgârı ${number(wind)} km/sa."
        if (gust != null && gust >= 70)
            severe += "Rüzgâr hamlesi ${number(gust)} km/sa seviyesine çıkabilir."
        if (rain != null && rain >= 50)
            severe +=
                "Günlük toplam yağış ${number(rain)} mm öngörülüyor; bu bir sel tahmini değildir."
        if (high != null && high >= 40) severe += "En yüksek sıcaklık ${number(high)} °C."
        if (high != null && high <= -10)
            severe += "Günün en yüksek sıcaklığı bile ${number(high)} °C."
        if (code == 67) severe += "Kuvvetli donan yağmur tahmini var."
        if (severe.isNotEmpty()) return DayAssessment(PlanLevel.SEVERE, severe)

        val knownCode = WeatherCondition.fromCode(code) != WeatherCondition.UNKNOWN
        val complete =
            knownCode &&
                high != null &&
                low != null &&
                high >= low &&
                chance != null &&
                chance in 0..100 &&
                wind != null &&
                wind >= 0 &&
                gust != null &&
                gust >= 0 &&
                rain != null &&
                rain >= 0 &&
                listOfNotNull(high, low, wind, gust, rain).all { it.isFinite() }
        if (!complete)
            return DayAssessment(
                null,
                listOf(
                    "Güvenilir bir planlama rengi için günlük sıcaklık, yağış veya rüzgâr verisi eksik/geçersiz.",
                ),
            )

        if (chance!! >= 65) indoor += "Günün en yüksek yağış olasılığı %$chance."
        if (rain!! >= 5) indoor += "Günlük toplam yağış ${number(rain)} mm."
        if (wind!! >= 30) indoor += "Günün en yüksek rüzgârı ${number(wind)} km/sa."
        if (gust!! >= 45) indoor += "Rüzgâr hamlesi ${number(gust)} km/sa olabilir."
        if (high!! >= 34 || high <= 8)
            indoor += "En yüksek sıcaklık ${number(high)} °C; açık hava konforu düşük olabilir."
        if (code in setOf(56, 57, 66, 71, 73, 75, 77, 85, 86))
            indoor += "${WeatherCondition.fromCode(code).label} tahmini var."
        if (indoor.isNotEmpty()) return DayAssessment(PlanLevel.INDOOR, indoor)

        if (chance >= 30)
            prepared += "Yağış olasılığı %$chance; şemsiye veya yağmurluk düşünebilirsin."
        if (wind >= 18 || gust >= 30)
            prepared += "Rüzgâr ${number(wind)} km/sa, hamleler ${number(gust)} km/sa."
        if (high < 16 || high >= 28 || low!! <= 5)
            prepared += "Sıcaklık ${number(low!!)}–${number(high)} °C; uygun katmanlarla hazırlan."
        if (rain > 0 || code in setOf(45, 48, 51, 53, 55, 61, 63, 65, 80, 81, 82))
            prepared +=
                "${WeatherCondition.fromCode(code).label}; kısa açık hava planında hazırlıklı ol."
        return if (prepared.isNotEmpty()) DayAssessment(PlanLevel.PREPARED, prepared)
        else
            DayAssessment(
                PlanLevel.OUTDOOR,
                listOf(
                    "Sıcaklık ${number(low!!)}–${number(high)} °C, yağış olasılığı %$chance ve rüzgâr ${number(wind)} km/sa; belirgin olumsuz sinyal yok.",
                ),
            )
    }
}
