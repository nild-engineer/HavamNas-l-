package com.example.hava1.domain.time

import com.example.hava1.model.*
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

object ForecastTimeline {
    fun localNow(forecast: Forecast): LocalDateTime? {
        val zone = forecast.timezone?.let { runCatching { ZoneId.of(it) }.getOrNull() }
        return zone?.let { LocalDateTime.now(it) } ?: forecast.current?.time?.let { parseTime(it) }
    }

    fun parseTime(value: String): LocalDateTime? = runCatching {
        LocalDateTime.parse(value)
    }
        .getOrNull()

    fun upcoming(
        forecast: Forecast,
        count: Int = 24,
        now: LocalDateTime? = localNow(forecast),
    ): List<HourlyWeather> {
        if (now == null) return emptyList()
        val start = now.truncatedTo(ChronoUnit.HOURS)
        return forecast.hourly
            .mapNotNull { hour ->
                parseTime(hour.time)?.let { it to hour }
            }
            .filter { !it.first.isBefore(start) }
            .sortedBy { it.first }
            .distinctBy { it.first }
            .take(count.coerceAtLeast(0))
            .map { it.second }
    }
}
