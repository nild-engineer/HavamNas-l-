package com.example.hava1

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.SystemClock
import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.test.core.app.ActivityScenario
import androidx.test.platform.app.InstrumentationRegistry
import com.example.hava1.ui.city.*
import com.example.hava1.ui.theme.Hava1Theme
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class DeliveryUiTest {
    private val instrumentation get()=InstrumentationRegistry.getInstrumentation()
    private fun find(node: AccessibilityNodeInfo?): AccessibilityNodeInfo? {
        if(node==null)return null
        if(node.text?.toString()=="Havam Nasıl?")return node
        for(i in 0 until node.childCount){val result=find(node.getChild(i));if(result!=null)return result}
        return null
    }
    @Test fun appNameReturnsToHome() {
        val query=mutableStateOf("Ankara")
        ActivityScenario.launch(ComponentActivity::class.java).use {scenario ->
            scenario.onActivity {activity ->activity.setContent {Hava1Theme {
                CitySearchScreen(query.value,if(query.value.isEmpty())CitySearchState.Idle else CitySearchState.Empty,{query.value=it},{},{})
            }} }
            instrumentation.waitForIdleSync();SystemClock.sleep(800)
            var node=find(instrumentation.uiAutomation.rootInActiveWindow)
            repeat(30){if(node==null){SystemClock.sleep(100);node=find(instrumentation.uiAutomation.rootInActiveWindow)}}
            assertNotNull(node)
            val bounds=Rect();node!!.getBoundsInScreen(bounds)
            instrumentation.uiAutomation.executeShellCommand("input tap ${bounds.centerX()} ${bounds.centerY()}").close()
            instrumentation.waitForIdleSync();SystemClock.sleep(500)
            assertEquals("",query.value)
        }
    }
    @Test fun launcherIconRendersWithCorrectLabel() {
        val context=instrumentation.targetContext
        assertEquals("Havam Nasıl?",context.packageManager.getApplicationLabel(context.applicationInfo).toString())
        val icon=context.packageManager.getApplicationIcon(context.packageName)
        val bitmap=Bitmap.createBitmap(384,384,Bitmap.Config.ARGB_8888)
        icon.setBounds(0,0,384,384);icon.draw(Canvas(bitmap))
        File(context.cacheDir,"delivery-icon.png").outputStream().use{bitmap.compress(Bitmap.CompressFormat.PNG,100,it)}
    }
}
