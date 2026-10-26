package com.example.hava1

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.example.hava1.model.*
import com.example.hava1.ui.forecast.ForecastScreen
import com.example.hava1.ui.forecast.ForecastState
import java.io.File
import org.junit.Assert.assertNotNull
import org.junit.Test

/** API 37 üzerinde eski Espresso sürümüne bağlı olmadan görsel kontrol. */
class WeatherUpgradeUiTest {
    private fun sample(code: Int, day: Boolean) =
        Forecast(
            "Europe/Istanbul",
            CurrentWeather("2026-10-25T12:00", 12.0, 10.0, 60, code, 12.0, day),
            (0..23).map {
                HourlyWeather("2099-10-25T${it.toString().padStart(2,'0')}:00", 12.0, code, 60)
            },
            (1..7).map {
                DailyWeather("2099-10-${it.toString().padStart(2,'0')}", 15.0, 8.0, code, 60)
            },
        )

    private fun render(code: Int, day: Boolean, city: String, filename: String) {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    MaterialTheme {
                        ForecastScreen(city, ForecastState.Success(sample(code, day)), {}, {})
                    }
                }
            }
            instrumentation.waitForIdleSync()
            SystemClock.sleep(600)
            val screenshot = instrumentation.uiAutomation.takeScreenshot()
            assertNotNull(screenshot)
            File(instrumentation.targetContext.cacheDir, filename).outputStream().use {
                screenshot.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }

    @Test
    fun sunnyForecastRenders() {
        render(0, true, "Ankara", "sunny-forecast.png")
    }

    @Test
    fun snowyNightRenders() {
        render(73, false, "Kars", "snowy-night.png")
    }
}
