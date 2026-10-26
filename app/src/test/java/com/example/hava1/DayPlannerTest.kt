package com.example.hava1

import com.example.hava1.data.mapper.toForecast
import com.example.hava1.data.remote.dto.*
import com.example.hava1.domain.planning.DayPlanner
import com.example.hava1.domain.planning.PlanLevel
import com.example.hava1.model.*
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class DayPlannerTest {
    private fun day() = DailyWeather("2026-10-26", 24.0, 14.0, 0, 10, 10.0, 15.0, 0.0)

    private fun level(d: DailyWeather) = DayPlanner.assess(d).level

    @Test
    fun mildCompleteDataIsOutdoor() {
        assertEquals(PlanLevel.OUTDOOR, level(day()))
    }

    @Test
    fun moderateWindRequiresPreparation() {
        assertEquals(PlanLevel.PREPARED, level(day().copy(maxWindSpeed = 18.0)))
    }

    @Test
    fun eightyPercentRainIsIndoorNotSevere() {
        val assessment = DayPlanner.assess(day().copy(maxWindSpeed = 18.0, rainProbability = 80))
        assertEquals(PlanLevel.INDOOR, assessment.level)
        assertTrue(assessment.reasons.any { it.contains("%80") })
    }

    @Test
    fun thunderstormIsSevere() {
        assertEquals(PlanLevel.SEVERE, level(day().copy(weatherCode = 95)))
    }

    @Test
    fun severeWindIsSevere() {
        assertEquals(PlanLevel.SEVERE, level(day().copy(maxWindSpeed = 60.0)))
    }

    @Test
    fun severeGustIsSevere() {
        assertEquals(PlanLevel.SEVERE, level(day().copy(maxWindGust = 70.0)))
    }

    @Test
    fun highRainTotalDoesNotClaimFlood() {
        val result = DayPlanner.assess(day().copy(precipitationSum = 50.0))
        assertEquals(PlanLevel.SEVERE, result.level)
        assertTrue(result.reasons.any { it.contains("sel tahmini değildir") })
    }

    @Test
    fun missingWindDoesNotBecomeGreen() {
        assertNull(level(day().copy(maxWindSpeed = null)))
    }

    @Test
    fun unknownCodeDoesNotBecomeGreen() {
        assertNull(level(day().copy(weatherCode = 999)))
    }

    @Test
    fun invalidNumericDataDoesNotBecomeGreen() {
        assertNull(level(day().copy(maxWindSpeed = Double.NaN)))
    }

    @Test
    fun provenStormStillWarnsWithPartialData() {
        assertEquals(PlanLevel.SEVERE, level(day().copy(weatherCode = 99, maxWindSpeed = null)))
    }

    @Test
    fun mapperKeepsDailyWindAndRainIndexes() {
        val response =
            Gson()
                .fromJson(
                    """{"daily":{"time":["2026-10-26","2026-10-27"],"wind_speed_10m_max":[18,60],"wind_gusts_10m_max":[25],"precipitation_sum":[0,50]}}""",
                    ForecastResponse::class.java,
                )
        val days = response.toForecast().daily
        assertEquals(18.0, days[0].maxWindSpeed!!, 0.001)
        assertEquals(60.0, days[1].maxWindSpeed!!, 0.001)
        assertNull(days[1].maxWindGust)
        assertEquals(50.0, days[1].precipitationSum!!, 0.001)
    }
}
