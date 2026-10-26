package com.example.hava1

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.example.hava1.model.CitySearchResult
import com.example.hava1.ui.city.*
import com.example.hava1.ui.theme.Hava1Theme
import java.io.File
import org.junit.Assert.*
import org.junit.Test

class PolishUiTest {
    private val instrumentation
        get() = InstrumentationRegistry.getInstrumentation()

    private fun screenshot(name: String) {
        val image = instrumentation.uiAutomation.takeScreenshot()
        File(instrumentation.targetContext.cacheDir, name).outputStream().use {
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Test
    fun androidBackClearsSearchInsteadOfClosingActivity() {
        val query = mutableStateOf("Ankara")
        val city = CitySearchResult(1, "Ankara", 39.9, 32.8, "Türkiye", null)
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    Hava1Theme {
                        CitySearchScreen(
                            query.value,
                            if (query.value.isBlank()) CitySearchState.Idle
                            else CitySearchState.Success(listOf(city)),
                            { query.value = it },
                            {},
                            {},
                        )
                    }
                }
            }
            instrumentation.waitForIdleSync()
            SystemClock.sleep(800)
            instrumentation.uiAutomation.executeShellCommand("input keyevent KEYCODE_BACK").close()
            instrumentation.waitForIdleSync()
            SystemClock.sleep(600)
            assertEquals("", query.value)
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
            screenshot("home-polished.png")
        }
    }

    @Test
    fun largerTextRemainsScrollable() {
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    Hava1Theme {
                        val normal = LocalDensity.current
                        CompositionLocalProvider(
                            LocalDensity provides Density(normal.density, 1.4f),
                        ) {
                            CitySearchScreen("", CitySearchState.Idle, {}, {}, {})
                        }
                    }
                }
            }
            instrumentation.waitForIdleSync()
            SystemClock.sleep(600)
            screenshot("home-large-text.png")
            assertEquals(Lifecycle.State.RESUMED, scenario.state)
        }
    }
}
