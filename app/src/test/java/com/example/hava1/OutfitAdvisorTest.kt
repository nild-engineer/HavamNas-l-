package com.example.hava1

import com.example.hava1.data.mapper.toForecast
import com.example.hava1.data.remote.dto.ForecastResponse
import com.example.hava1.domain.recommendation.OutfitAdvisor
import com.example.hava1.domain.time.ForecastTimeline
import com.example.hava1.model.*
import com.google.gson.Gson
import java.time.LocalDateTime
import org.junit.Assert.*
import org.junit.Test

class OutfitAdvisorTest {
    private val now = LocalDateTime.parse("2026-10-25T12:30")

    private fun forecast(
        code: Int? = 0,
        temp: Double? = 20.0,
        day: Boolean? = true,
        wind: Double? = 5.0,
        hours: List<HourlyWeather> = emptyList(),
    ) =
        Forecast(
            "Europe/Istanbul",
            CurrentWeather("2026-10-25T12:15", temp, temp, 60, code, wind, day),
            hours,
            emptyList(),
        )

    private fun tips(f: Forecast) = OutfitAdvisor.advise(f, now)

    @Test
    fun coldWeatherSuggestsWarmLayers() {
        assertTrue(tips(forecast(temp = 2.0)).any { it.title.contains("Mont") })
    }

    @Test
    fun sunnyDaySuggestsGlassesButNightDoesNot() {
        assertTrue(tips(forecast()).any { it.icon == "🕶️" })
        assertFalse(tips(forecast(day = false)).any { it.icon == "🕶️" })
        assertFalse(tips(forecast(day = null)).any { it.icon == "🕶️" })
    }

    @Test
    fun stormDoesNotSuggestUmbrella() {
        val result = tips(forecast(code = 95))
        assertTrue(result.any { it.icon == "⛈️" })
        assertFalse(result.any { it.icon == "☂️" })
    }

    @Test
    fun rainWithWindSuggestsRaincoat() {
        val result = tips(forecast(code = 61, wind = 40.0))
        assertTrue(result.any { it.title.contains("yağmurluk", ignoreCase = true) })
        assertFalse(result.any { it.icon == "☂️" })
    }

    @Test
    fun snowSuggestsSuitableFootwear() {
        assertTrue(tips(forecast(code = 73)).any { it.icon == "🥾" })
    }

    @Test
    fun futureRainSuggestsUmbrella() {
        val hour = HourlyWeather("2026-10-25T14:00", 18.0, 3, 80)
        assertTrue(tips(forecast(hours = listOf(hour))).any { it.icon == "☂️" })
    }

    @Test
    fun pastRainDoesNotCauseUmbrellaAdvice() {
        val hour = HourlyWeather("2026-10-25T08:00", 18.0, 61, 90)
        assertFalse(tips(forecast(hours = listOf(hour))).any { it.icon == "☂️" })
    }

    @Test
    fun missingDataDoesNotInventAdvice() {
        val f = Forecast(null, null, emptyList(), emptyList())
        assertEquals("Öneri İçin Veri Yetersiz", tips(f).single().title)
    }

    @Test
    fun hoursAreSortedAndMidnightWorks() {
        val f =
            forecast(
                hours =
                    listOf(
                        HourlyWeather("2026-10-26T00:00", 10.0, 0, 0),
                        HourlyWeather("not-a-date", 10.0, 0, 0),
                        HourlyWeather("2026-10-25T23:00", 10.0, 0, 0),
                        HourlyWeather("2026-10-25T22:00", 10.0, 0, 0),
                    ),
            )
        val result = ForecastTimeline.upcoming(f, now = LocalDateTime.parse("2026-10-25T23:30"))
        assertEquals(listOf("2026-10-25T23:00", "2026-10-26T00:00"), result.map { it.time })
    }

    @Test
    fun dayNightApiMappingIsCorrect() {
        fun parse(value: String) =
            Gson()
                .fromJson("{\"current\":{\"is_day\":$value}}", ForecastResponse::class.java)
                .toForecast()
                .current
                ?.isDay
        assertEquals(true, parse("1"))
        assertEquals(false, parse("0"))
        assertNull(parse("8"))
    }

    @Test
    fun distantFutureRainDoesNotAffectNextSixHours() {
        val hour = HourlyWeather("2026-10-26T14:00", 18.0, 61, 90)
        assertFalse(tips(forecast(hours = listOf(hour))).any { it.icon == "☂️" })
    }
}
