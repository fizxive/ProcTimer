package com.imaginaryrhombus.proctimer

import com.imaginaryrhombus.proctimer.ui.timer.TimerViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TimerViewModelTest {

    /// テスト用の Activity.
    private val testActivity = Robolectric.setupActivity(TimerActivity::class.java)!!

    /// 秒数が正常にフォーマットされているか.
    @Test
    fun testText() {

        val context = testActivity.applicationContext

        fun testImpl(seconds : Float, timerText : String) {
            TimerModelTestUtility.setTimerSecondsToSharedPreferences(seconds, context)
            TimerViewModel(testActivity.application).run {
                assertEquals(this.timerText.value, timerText)
            }
        }

        testImpl(30.0f, "00:30.000")
        testImpl(60.0f, "01:00.000")
        testImpl(75.0f, "01:15.000")
        testImpl(0.0f, "00:00.000")
        testImpl(-10.0f, "00:00.000")
        testImpl(10.110f, "00:10.110")
        testImpl(60.085f, "01:00.085")
        testImpl(66.666f, "01:06.666")
    }
}
