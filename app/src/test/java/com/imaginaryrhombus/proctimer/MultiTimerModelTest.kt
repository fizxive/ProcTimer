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

    /**
     * SharedPreferences への保存/読み出しが正常か確認する.
     */
    @Test
    fun testSaveAndRestore() {
        _sharedPreferences.edit().clear()

        val multiTimerModel = MultiTimerModel(_testActivity)

        assertTrue(_sharedPreferences.contains(TimerConstants.PREFERENCE_SAVE_VERSION_NAME))
        assertEquals(_sharedPreferences.getInt(TimerConstants.PREFERENCE_SAVE_VERSION_NAME, TimerConstants.PREFERENCE_SAVE_VERSION_INVALID), TimerConstants.PREFERENCE_SAVE_VERSION)
        assertNotEquals(_sharedPreferences.getString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, ""), "")

        multiTimerModel.addTimer()
        multiTimerModel.setActiveTimerSeconds(10.0f)

        val multiTimerModel2 = MultiTimerModel(_testActivity)

        val timers = multiTimerModel.getTimers(-1, true)
        val timers2 = multiTimerModel2.getTimers(-1, true)

        assertEquals(timers.size, timers2.size)

        for (index in 0 until timers.size) {
            assertEquals(timers[index]?.seconds, timers2[index]?.seconds)
            assertEquals(timers[index]?.defaultSeconds, timers2[index]?.defaultSeconds)
        }
    }

}
