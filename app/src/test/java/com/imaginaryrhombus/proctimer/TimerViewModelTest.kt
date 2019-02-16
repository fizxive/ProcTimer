package com.imaginaryrhombus.proctimer

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimerViewModelTest {

    init {
        // JvmStatic, BeforeClass だとランタイムエラーを起こす.
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext<Application>())
    }

    /**
     * Activity 生成のための Rule.
     */
    @JvmField @Rule
    val scenarioRule = ActivityScenarioRule(TimerActivity::class.java)

    /**
     * 秒数が正常にフォーマットされているか.
     */
    @Test
    fun testText() {
        //launch(TimerActivity::class.java).use { scenario ->
        scenarioRule.scenario.use { scenario ->
            scenario.onActivity {
                val timerViewModel = TimerViewModel(it.application)
                val method =
                    timerViewModel.javaClass.getMethod("createTimerStringFromSeconds", Float::class.java)

                assertNotNull(method)
                method.isAccessible = true

                assertEquals(method.invoke(null, 30.0f), "00:30.000")
                assertEquals(method.invoke(null, 60.0f), "01:00.000")
                assertEquals(method.invoke(null, 75.0f), "01:15.000")
                assertEquals(method.invoke(null, 0.0f), "00:00.000")
                assertEquals(method.invoke(null, -10.0f), "00:00.000")
                assertEquals(method.invoke(null, 10.110f), "00:10.110")
                assertEquals(method.invoke(null, 60.085f), "01:00.085")
                assertEquals(method.invoke(null, 66.666f), "01:06.666")
            }
        }
    }
}
