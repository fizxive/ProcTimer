package com.imaginaryrhombus.proctimer

import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimerViewModelTest {

    /**
     * テスト用の Activity.
     */
    private val testActivity = Robolectric.setupActivity(TimerActivity::class.java)!!

    /**
     * 秒数が正常にフォーマットされているか.
     */
    @Test
    fun testText() {

        val timerViewModel = TimerViewModel(testActivity.application)
        val method = timerViewModel.javaClass.getMethod("createTimerStringFromSeconds", Float::class.java)

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
