package com.example.hava1.domain.recommendation

import com.example.hava1.domain.time.ForecastTimeline
import com.example.hava1.model.*
import java.time.LocalDateTime

data class OutfitTip(val icon: String, val title: String, val reason: String)

/** Açıklanabilir öneri kuralları; sıcaklık eşikleri ürün tercihidir. */
object OutfitAdvisor {
    fun advise(
        forecast: Forecast,
        now: LocalDateTime? = ForecastTimeline.localNow(forecast),
    ): List<OutfitTip> {
        val current = forecast.current
        val hours =
            ForecastTimeline.upcoming(forecast, 6, now).filter { hour ->
                val time = ForecastTimeline.parseTime(hour.time)
                now != null && time != null && time.isBefore(now.plusHours(6))
            }
        val codes = listOfNotNull(current?.weatherCode) + hours.mapNotNull { it.weatherCode }
        val feels = current?.feelsLike ?: current?.temperature
        val temperatures = listOfNotNull(feels) + hours.mapNotNull { it.temperature }
        val low = temperatures.minOrNull()
        val high = temperatures.maxOrNull()
        val probability = hours.mapNotNull { it.rainProbability }.maxOrNull()
        val tips = mutableListOf<OutfitTip>()
        if (low != null) {
            tips +=
                when {
                    low <= 5 ->
                        OutfitTip(
                            "🧥",
                            "Mont ve Sıcak Katmanlar",
                            "Anlık veya önümüzdeki saatlerde sıcaklık 5 °C ve altında; eldiven de iyi gelebilir.",
                        )
                    low <= 15 ->
                        OutfitTip(
                            "🧣",
                            "Bir Katman Yanına Al",
                            "Anlık veya önümüzdeki saatlerde sıcaklık 15 °C ve altında; ceket veya hırka düşünebilirsin.",
                        )
                    low <= 22 ->
                        OutfitTip(
                            "👕",
                            "Hafif Katmanlar",
                            "Hava ılıman görünüyor; ince bir üst değişen sıcaklıkta işine yarayabilir.",
                        )
                    else ->
                        OutfitTip(
                            "👕",
                            "Hafif ve Nefes Alan Giysiler",
                            "Veriler sıcak bir havaya işaret ediyor; rahat ve hafif parçalar seçebilirsin.",
                        )
                }
        }
        val snow = codes.any { it in setOf(71, 73, 75, 77, 85, 86) }
        val freezing = codes.any { it in setOf(56, 57, 66, 67) }
        val storm = codes.any { it in setOf(95, 96, 99) }
        val rain = codes.any { it in setOf(51, 53, 55, 61, 63, 65, 80, 81, 82) }
        if (snow || freezing)
            tips +=
                OutfitTip(
                    "🥾",
                    "Suya Dayanıklı ve Tutuşlu Ayakkabı",
                    "Kar veya donan yağış bekleniyor; kaygan zemine dikkat et.",
                )
        if (storm) {
            tips +=
                OutfitTip(
                    "⛈️",
                    "Dışarı Planını Gözden Geçir",
                    "Gök gürültülü fırtına öngörülüyor; açık alanda kalmak yerine kapalı bir yer tercih et.",
                )
        } else if (rain || freezing || (!snow && (probability ?: 0) >= 50)) {
            val windy = (current?.windSpeed ?: 0.0) >= 35
            tips +=
                if (windy)
                    OutfitTip(
                        "🧥",
                        "Kapüşonlu Yağmurluk",
                        "Yağış ve kuvvetli rüzgâr birlikte görünüyor; şemsiye kullanmak zorlaşabilir.",
                    )
                else
                    OutfitTip(
                        "☂️",
                        "Şemsiyeni Yanına Al",
                        "Anlık yağış veya önümüzdeki saatlerde yağış sinyali var; suya dayanıklı ayakkabı da düşünebilirsin.",
                    )
        }
        if ((current?.windSpeed ?: 0.0) >= 35)
            tips +=
                OutfitTip(
                    "🌬️",
                    "Rüzgârı Kesen Bir Üst",
                    "Anlık rüzgâr en az 35 km/sa; kolay uçan aksesuarları sabitle.",
                )
        if (current?.isDay == true && current.weatherCode in setOf(0, 1, 2)) {
            tips +=
                OutfitTip(
                    "🕶️",
                    "Güneş Gözlüğü veya Şapka",
                    "Gündüz ve açık/parçalı bulutlu hava görünüyor. Bu öneri UV ölçümüne dayanmıyor.",
                )
        }
        if ((high ?: 0.0) >= 28)
            tips +=
                OutfitTip(
                    "💧",
                    "Su Şişeni Unutma",
                    "Anlık veya önümüzdeki saatlerde sıcaklık 28 °C ve üzerine çıkıyor.",
                )
        if (tips.isEmpty())
            tips +=
                OutfitTip(
                    "ℹ️",
                    "Öneri İçin Veri Yetersiz",
                    "Giyim önerisi oluşturacak sıcaklık veya hava durumu bilgisi gelmedi.",
                )
        return tips
    }
}
