package com.example.hava1

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Rect
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.example.hava1.data.local.RecentCityStore
import com.example.hava1.domain.history.RecentCityHistory
import com.example.hava1.model.*
import com.example.hava1.ui.city.*
import java.io.File
import org.junit.Assert.*
import org.junit.Test

class HomeUpgradeUiTest {
    private val instrumentation
        get() = InstrumentationRegistry.getInstrumentation()

    private fun city(id: Int, name: String) =
        CitySearchResult(id, name, 40.15, 26.4, "Türkiye", null)

    private fun find(
        node: AccessibilityNodeInfo?,
        predicate: (AccessibilityNodeInfo) -> Boolean,
    ): AccessibilityNodeInfo? {
        if (node == null) return null
        if (predicate(node)) return node
        for (i in 0 until node.childCount) {
            val r = find(node.getChild(i), predicate)
            if (r != null) return r
        }
        return null
    }

    private fun tap(node: AccessibilityNodeInfo?) {
        assertNotNull(node)
        val bounds = Rect()
        node!!.getBoundsInScreen(bounds)
        instrumentation.uiAutomation
            .executeShellCommand("input tap ${bounds.centerX()} ${bounds.centerY()}")
            .close()
        instrumentation.waitForIdleSync()
        SystemClock.sleep(300)
    }

    private fun screenshot(name: String) {
        val image = instrumentation.uiAutomation.takeScreenshot()
        File(instrumentation.targetContext.cacheDir, name).outputStream().use {
            image.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    @Test
    fun firstVisitRendersWithoutRecentSection() {
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    MaterialTheme {
                        CitySearchScreen("", CitySearchState.Idle, {}, {}, {})
                    }
                }
            }
            instrumentation.waitForIdleSync()
            SystemClock.sleep(600)
            var brand: AccessibilityNodeInfo? = null
            repeat(40) {
                if (brand == null) {
                    brand =
                        find(instrumentation.uiAutomation.rootInActiveWindow) {
                            it.text?.toString() == "Havam Nasıl?"
                        }
                    if (brand == null) SystemClock.sleep(100)
                }
            }
            assertNotNull(brand)
            assertNull(
                find(instrumentation.uiAutomation.rootInActiveWindow) {
                    it.text?.toString() == "Son Aramalar"
                },
            )
            screenshot("home-first-visit.png")
        }
    }

    @Test
    fun recentCityCanBeDeletedWithoutOpeningForecast() {
        val recent = mutableStateOf(listOf(city(1, "Çanakkale"), city(2, "İzmir")))
        var selected: CitySearchResult? = null
        ActivityScenario.launch(ComponentActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent {
                    MaterialTheme {
                        CitySearchScreen(
                            "",
                            CitySearchState.Idle,
                            {},
                            {},
                            { selected = it },
                            recentCities = recent.value,
                            onRemoveRecent = { city ->
                                recent.value = RecentCityHistory.remove(recent.value, city.id)
                            },
                        )
                    }
                }
            }
            instrumentation.waitForIdleSync()
            SystemClock.sleep(600)
            screenshot("home-recent-cities.png")
            tap(
                find(instrumentation.uiAutomation.rootInActiveWindow) {
                    it.contentDescription?.toString() == "Çanakkale şehrini son aramalardan sil"
                },
            )
            assertEquals(listOf(2), recent.value.map { it.id })
            assertNull(selected)
            tap(
                find(instrumentation.uiAutomation.rootInActiveWindow) {
                    it.text?.toString() == "İzmir"
                },
            )
            assertEquals(2, selected?.id)
        }
    }

    @Test
    fun storeRestoresAndKeepsDeletion() {
        val base = instrumentation.targetContext
        val prefsName = "test_recent_${System.nanoTime()}"
        val context =
            object : ContextWrapper(base) {
                override fun getApplicationContext(): Context = this

                override fun getSharedPreferences(name: String, mode: Int) =
                    base.getSharedPreferences(prefsName, mode)
            }
        try {
            RecentCityStore(context).record(city(1, "Çanakkale"))
            val restored = RecentCityStore(context)
            assertEquals("Çanakkale", restored.cities.value.single().name)
            restored.remove(1)
            assertTrue(RecentCityStore(context).cities.value.isEmpty())
        } finally {
            base.deleteSharedPreferences(prefsName)
        }
    }
}
