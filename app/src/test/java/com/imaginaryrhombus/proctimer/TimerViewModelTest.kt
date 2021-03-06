package com.imaginaryrhombus.proctimer

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.get
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
class TimerViewModelTest : AutoCloseKoinTest() {

    /**
     * Activity 生成のための Rule.
     */
    @JvmField
    @Rule
    val scenarioRule = ActivityScenarioRule(TimerActivity::class.java)

    /**
     * 秒数が正常にフォーマットされているか.
     */
    @Test
    fun testText() {
        val timerViewModel = get<TimerViewModel>()
        val method =
            timerViewModel.javaClass.getMethod(
                "createTimerStringFromSeconds", Float::class.java
            )

        assertNotNull(method)
        method.isAccessible = true

        assertEquals("00:30", method.invoke(null, 30.0f))
        assertEquals("01:00", method.invoke(null, 60.0f))
        assertEquals("01:15", method.invoke(null, 75.0f))
        assertEquals("00:00", method.invoke(null, 0.0f))
        assertEquals("00:00", method.invoke(null, -10.0f))
        assertEquals("00:11", method.invoke(null, 10.110f))
        assertEquals("01:01", method.invoke(null, 60.085f))
        assertEquals("01:07", method.invoke(null, 66.666f))
    }
}
