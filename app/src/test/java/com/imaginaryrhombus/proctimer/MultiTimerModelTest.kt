package com.imaginaryrhombus.proctimer

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import com.imaginaryrhombus.proctimer.ui.timer.MultiTimerModel
import com.imaginaryrhombus.proctimer.ui.timer.TimerModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MultiTimerModelTest {

    /**
     * テスト用の Activity.
     */
    private val _testActivity = Robolectric.setupActivity(TimerActivity::class.java)!!

    /**
     * テスト用の SharedPreferences.
     */
    private val _sharedPreferences : SharedPreferences = _testActivity.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    /**
     * まっさらな状態からインスタンスを生成したときの動作を確認する.
     */
    @Test
    fun testCreate() {
        _sharedPreferences.edit().clear()

        val multiTimerModel = MultiTimerModel(_testActivity)

        val firstTimerModel = multiTimerModel.activeTimerModel
        assertEquals(firstTimerModel.seconds.value, TimerConstants.TIMER_DEFAULT_SECONDS)

        val timers = multiTimerModel.getTimers(999, true)
        assert(timers.first() === firstTimerModel)

        assertTrue(timers.all {
            val firstTest = it === timers.first() && it != null
            val otherTest = it !== timers.first() && it == null
            firstTest || otherTest
        })
    }

}
