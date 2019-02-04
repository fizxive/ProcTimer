package com.imaginaryrhombus.proctimer

import android.content.Context
import android.content.SharedPreferences
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import com.imaginaryrhombus.proctimer.ui.timer.MultiTimerModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MultiTimerModelTest {

    /**
     * テスト用の Activity.
     */
    private val testActivity =
        checkNotNull(Robolectric.setupActivity(TimerActivity::class.java))
            {"Activity creation for test failed."}

    /**
     * テスト用の SharedPreferences.
     */
    private val sharedPreferences: SharedPreferences =
        testActivity.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    /**
     * まっさらな状態からインスタンスを生成したときの動作を確認する.
     */
    @Test
    fun testCreate() {
        sharedPreferences.edit().clear()

        val multiTimerModel = MultiTimerModel(testActivity)

        val firstTimerModel = multiTimerModel.activeTimerModel
        assertEquals(firstTimerModel.seconds.value, TimerConstants.TIMER_DEFAULT_SECONDS)

        val timers = multiTimerModel.getTimers(-1, true)
        assertEquals(timers.size, TimerConstants.TIMER_DEFAULT_COUNTS)

        timers.forEach {
            assertNotNull(it)
            assertEquals(it?.seconds?.value, TimerConstants.TIMER_DEFAULT_SECONDS)
            assertEquals(it?.defaultSeconds, TimerConstants.TIMER_DEFAULT_SECONDS)
        }
    }

    /**
     * タイマー取得の挙動を確認する.
     */
    @Test
    fun testGetTimer() {
        sharedPreferences.edit().clear()

        val multiTimerModel = MultiTimerModel(testActivity)

        multiTimerModel.addTimer()
        multiTimerModel.setActiveTimerSeconds(30.0f)
        multiTimerModel.next()
        multiTimerModel.setActiveTimerSeconds(60.0f)
        multiTimerModel.next()
        multiTimerModel.setActiveTimerSeconds(90.0f)
        multiTimerModel.next()

        /**
         * この時点で、以下のような並びになっているはず.
         * [30.0f], [60.0f], [90.0f]
         */

        multiTimerModel.getTimers(-1, true).run {
            assertEquals(size, 3)
            assertNotNull(get(0))
            assertNotNull(get(1))
            assertNotNull(get(2))
            assertEquals(get(0)?.seconds?.value, 30.0f)
            assertEquals(get(1)?.seconds?.value, 60.0f)
            assertEquals(get(2)?.seconds?.value, 90.0f)
            assertEquals(get(0)?.defaultSeconds, 30.0f)
            assertEquals(get(1)?.defaultSeconds, 60.0f)
            assertEquals(get(2)?.defaultSeconds, 90.0f)
        }

        multiTimerModel.getTimers(3, true).run {
            assertEquals(size, 3)
            assertNotNull(get(0))
            assertNotNull(get(1))
            assertNotNull(get(2))
            assertEquals(get(0)?.seconds?.value, 30.0f)
            assertEquals(get(1)?.seconds?.value, 60.0f)
            assertEquals(get(2)?.seconds?.value, 90.0f)
            assertEquals(get(0)?.defaultSeconds, 30.0f)
            assertEquals(get(1)?.defaultSeconds, 60.0f)
            assertEquals(get(2)?.defaultSeconds, 90.0f)
        }

        multiTimerModel.getTimers(3).run {
            assertEquals(size, 3)
            assertNotNull(get(0))
            assertNotNull(get(1))
            assertNull(get(2))
            assertEquals(get(0)?.seconds?.value, 60.0f)
            assertEquals(get(1)?.seconds?.value, 90.0f)
            assertEquals(get(0)?.defaultSeconds, 60.0f)
            assertEquals(get(1)?.defaultSeconds, 90.0f)
        }
    }

    /**
     * SharedPreferences への保存/読み出しが正常か確認する.
     */
    @Test
    fun testSaveAndRestore() {
        sharedPreferences.edit().clear()

        val multiTimerModel = MultiTimerModel(testActivity)

        assertTrue(sharedPreferences.contains(TimerConstants.PREFERENCE_SAVE_VERSION_NAME))
        assertEquals(
            sharedPreferences.getInt(TimerConstants.PREFERENCE_SAVE_VERSION_NAME,
                TimerConstants.PREFERENCE_SAVE_VERSION_INVALID),
            TimerConstants.PREFERENCE_SAVE_VERSION)
        assertNotEquals(
            sharedPreferences.getString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, ""),
            "")

        multiTimerModel.addTimer()
        multiTimerModel.setActiveTimerSeconds(10.0f)

        val multiTimerModel2 = MultiTimerModel(testActivity)

        val timers = multiTimerModel.getTimers(-1, true)
        val timers2 = multiTimerModel2.getTimers(-1, true)

        assertEquals(timers.size, timers2.size)

        for (index in 0 until timers.size) {
            assertEquals(timers[index]?.seconds?.value, timers2[index]?.seconds?.value)
            assertEquals(timers[index]?.defaultSeconds, timers2[index]?.defaultSeconds)
        }
    }
}
