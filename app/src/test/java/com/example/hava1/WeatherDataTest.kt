package com.example.hava1

import com.example.hava1.data.mapper.toForecast
import com.example.hava1.data.remote.dto.ForecastResponse
import com.example.hava1.model.WeatherCondition
import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Test

class WeatherDataTest {

    private fun parse(json: String): ForecastResponse {
        return Gson().fromJson(json, ForecastResponse::class.java)
    }

    @Test
    fun emptyResponseDoesNotCrash() {
        val forecast = parse("{}").toForecast()

        assertNull(forecast.current)
        assertTrue(forecast.hourly.isEmpty())
        assertTrue(forecast.daily.isEmpty())
    }

    @Test
    fun jsonFieldsAreMappedCorrectly() {
        val forecast =
            parse(
                    """
                    {
                      "current": {
                        "temperature_2m": 12.5,
                        "apparent_temperature": 10.0,
                        "relative_humidity_2m": 65,
                        "weather_code": 61,
                        "wind_speed_10m": 18.0
                      }
                    }
                    """
                        .trimIndent(),
                )
                .toForecast()

        val current = requireNotNull(forecast.current)

        assertEquals(12.5, requireNotNull(current.temperature), 0.001)
        assertEquals(10.0, requireNotNull(current.feelsLike), 0.001)
        assertEquals(65, requireNotNull(current.humidity))
        assertEquals(61, requireNotNull(current.weatherCode))
        assertEquals(18.0, requireNotNull(current.windSpeed), 0.001)
    }

    @Test
    fun shortHourlyListsDoNotShiftValuesOrCrash() {
        val forecast =
            parse(
                    """
                    {
                      "hourly": {
                        "time": [
                          "2026-10-25T10:00",
                          "2026-10-25T11:00"
                        ],
                        "temperature_2m": [12.5],
                        "weather_code": [0, 61],
                        "precipitation_probability": [10, 80]
                      }
                    }
                    """
                        .trimIndent(),
                )
                .toForecast()

        assertEquals(2, forecast.hourly.size)
        assertEquals(
            12.5,
            requireNotNull(forecast.hourly[0].temperature),
            0.001,
        )
        assertNull(forecast.hourly[1].temperature)
        assertEquals(61, requireNotNull(forecast.hourly[1].weatherCode))
        assertEquals(
            80,
            requireNotNull(forecast.hourly[1].rainProbability),
        )
    }

    @Test
    fun skippingBlankTimeKeepsOriginalIndexes() {
        val forecast =
            parse(
                    """
                    {
                      "hourly": {
                        "time": ["", "2026-10-25T11:00"],
                        "temperature_2m": [99.0, 15.0]
                      }
                    }
                    """
                        .trimIndent(),
                )
                .toForecast()

        assertEquals(1, forecast.hourly.size)
        assertEquals(
            15.0,
            requireNotNull(forecast.hourly.single().temperature),
            0.001,
        )
    }

    @Test
    fun dailyMinimumAndMaximumAreNotSwapped() {
        val forecast =
            parse(
                    """
                    {
                      "daily": {
                        "time": ["2026-10-25", "2026-10-26"],
                        "temperature_2m_max": [22.0, 24.0],
                        "temperature_2m_min": [8.0],
                        "precipitation_probability_max": [0, 100]
                      }
                    }
                    """
                        .trimIndent(),
                )
                .toForecast()

        assertEquals(2, forecast.daily.size)
        assertEquals(
            22.0,
            requireNotNull(forecast.daily[0].maxTemperature),
            0.001,
        )
        assertEquals(
            8.0,
            requireNotNull(forecast.daily[0].minTemperature),
            0.001,
        )
        assertNull(forecast.daily[1].minTemperature)
        assertEquals(
            100,
            requireNotNull(forecast.daily[1].rainProbability),
        )
    }

    @Test
    fun invalidMeasurementsAreNotShownAsRealValues() {
        val forecast =
            parse(
                    """
                    {
                      "current": {
                        "temperature_2m": -5.0,
                        "relative_humidity_2m": 140,
                        "wind_speed_10m": -3.0
                      },
                      "hourly": {
                        "time": ["2026-10-25T10:00"],
                        "precipitation_probability": [-1]
                      }
                    }
                    """
                        .trimIndent(),
                )
                .toForecast()

        val current = requireNotNull(forecast.current)

        // Negatif sıcaklık geçerlidir; negatif rüzgâr hızı değildir.
        assertEquals(-5.0, requireNotNull(current.temperature), 0.001)
        assertNull(current.humidity)
        assertNull(current.windSpeed)
        assertNull(forecast.hourly.single().rainProbability)
    }

    @Test
    fun weatherCodesHaveSafeFallbacks() {
        assertEquals(WeatherCondition.CLEAR, WeatherCondition.fromCode(0))
        assertEquals(WeatherCondition.RAIN, WeatherCondition.fromCode(61))
        assertEquals(
            WeatherCondition.FREEZING_RAIN,
            WeatherCondition.fromCode(66),
        )
        assertEquals(
            WeatherCondition.THUNDERSTORM_HAIL,
            WeatherCondition.fromCode(99),
        )
        assertEquals(
            WeatherCondition.UNKNOWN,
            WeatherCondition.fromCode(999),
        )
        assertEquals(
            WeatherCondition.UNKNOWN,
            WeatherCondition.fromCode(null),
        )
    }
}
