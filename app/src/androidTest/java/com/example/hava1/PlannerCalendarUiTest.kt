package com.example.hava1

import android.graphics.Bitmap
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.example.hava1.model.*
import com.example.hava1.ui.forecast.*
import java.io.File
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.Locale
import org.junit.Assert.*
import org.junit.Test

class PlannerCalendarUiTest {
    private fun find(
        node: AccessibilityNodeInfo?,
        predicate: (AccessibilityNodeInfo) -> Boolean,
    ): AccessibilityNodeInfo? {
        if (node == null) return null
        if (predicate(node)) return node
        for (i in 0 until node.childCount) {
            val result = find(node.getChild(i), predicate)
            if (result != null) return result
        }
        return null
    }

    @Test
    fun swipeOpensCalendarAndDayExplainsAdvice() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val automation = instrumentation.uiAutomation
        val today = LocalDate.now(ZoneId.of("Europe/Istanbul"))
        val forecast =
            Forecast(
                "Europe/Istanbul",
                CurrentWeather(today.toString() + "T12:00", 20.0, 20.0, 60, 0, 10.0, true),
                emptyList(),
                (0..15).map { index ->
                    DailyWeather(
                        today.plusDays(index.toLong()).toString(),
                        24.0,
                        14.0,
                        if (index % 4 == 3) 95 else 0,
                        if (index % 4 == 2) 80 else 10,
                        if (index % 4 == 1) 20.0 else 10.0,
                        15.0,
                        0.0,
                    )
                },
            )
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    MaterialTheme {
                        ForecastScreen(
                            "Çanakkale",
                            ForecastState.Success(forecast),
                            {},
                            {},
                            updatedAt = Instant.now().toString(),
                        )
                    }
                }
            }
            instrumentation.waitForIdleSync()
            SystemClock.sleep(600)
            val width = automation.takeScreenshot().width
            automation.executeShellCommand("input swipe ${width-100} 700 100 700 450").close()
            instrumentation.waitForIdleSync()
            SystemClock.sleep(700)
            assertNotNull(
                find(automation.rootInActiveWindow) { it.text?.toString() == "Havaya Göre Planla" },
            )
            val screen = automation.takeScreenshot()
            File(instrumentation.targetContext.cacheDir, "planner-calendar.png")
                .outputStream()
                .use { screen.compress(Bitmap.CompressFormat.PNG, 100, it) }
            val label =
                today.format(
                    DateTimeFormatter.ofPattern("d MMMM yyyy EEEE", Locale.forLanguageTag("tr-TR")),
                )
            val cell =
                find(automation.rootInActiveWindow) {
                    it.contentDescription?.toString()?.startsWith(label + ":") == true
                }
            assertNotNull(cell)
            val bounds = android.graphics.Rect()
            cell!!.getBoundsInScreen(bounds)
            automation
                .executeShellCommand("input tap ${bounds.centerX()} ${bounds.centerY()}")
                .close()
            instrumentation.waitForIdleSync()
            SystemClock.sleep(400)
            assertNotNull(find(automation.rootInActiveWindow) { it.text?.toString() == "Tamam" })
            val detail = automation.takeScreenshot()
            File(instrumentation.targetContext.cacheDir, "planner-detail.png").outputStream().use {
                detail.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }
}
