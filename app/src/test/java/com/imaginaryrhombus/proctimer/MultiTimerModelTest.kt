package com.imaginaryrhombus.proctimer

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imaginaryrhombus.proctimer.application.TimerComponentInterface
import com.imaginaryrhombus.proctimer.application.TimerSharedPreferencesComponent
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import com.imaginaryrhombus.proctimer.ui.timer.MultiTimerModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule
import org.koin.core.get
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
class MultiTimerModelTest : AutoCloseKoinTest() {

    /**
     * Activity 生成のための Rule.
     */
    @JvmField
    @Rule
    val scenarioRule = ActivityScenarioRule(TimerActivity::class.java)

    private val sharedPreferencesComponent: TimerSharedPreferencesComponent = get()

    /**
     * まっさらな状態からインスタンスを生成したときの動作を確認する.
     */
    @Test
    fun testCreate() {
        sharedPreferencesComponent.reset()

        val multiTimerModel = MultiTimerModel(get())

        assertEquals(TimerConstants.TIMER_DEFAULT_SECONDS, multiTimerModel.activeTimerSeconds)

        val timers = multiTimerModel.timerList
        assertEquals(TimerConstants.TIMER_DEFAULT_COUNTS, timers.size)

        timers.forEach {
            assertEquals(TimerConstants.TIMER_DEFAULT_SECONDS, it.seconds.value)
            assertEquals(TimerConstants.TIMER_DEFAULT_SECONDS, it.defaultSeconds)
        }
    }

    /**
     * タイマー取得の挙動を確認する.
     */
    @Test
    fun testGetTimer() {
        sharedPreferencesComponent.reset()

        val multiTimerModel = MultiTimerModel(get())
        multiTimerModel.run {
            addTimer()
            activeTimerSeconds = 30.0f
            next()
            activeTimerSeconds = 60.0f
            next()
            activeTimerSeconds = 90.0f
            next()
        }

        /**
         * この時点で、以下のような並びになっているはず.
         * [30.0f], [60.0f], [90.0f]
         */

        multiTimerModel.timerList.run {
            assertEquals(3, size)
            assertEquals(30.0f, get(0).seconds.value)
            assertEquals(60.0f, get(1).seconds.value)
            assertEquals(90.0f, get(2).seconds.value)
            assertEquals(30.0f, get(0).defaultSeconds)
            assertEquals(60.0f, get(1).defaultSeconds)
            assertEquals(get(2).defaultSeconds, 90.0f)
        }
    }

    /**
     * SharedPreferences への保存/読み出しが正常か確認する.
     */
    @Test
    fun testSaveAndRestore() {
        sharedPreferencesComponent.reset()

        val sharedPreferences = get<TimerComponentInterface>().sharedPreferences

        val multiTimerModel = MultiTimerModel(get())

        assertTrue(sharedPreferences.contains(TimerConstants.PREFERENCE_SAVE_VERSION_NAME))
        assertEquals(
            TimerConstants.PREFERENCE_SAVE_VERSION,
            sharedPreferences.getInt(
                TimerConstants.PREFERENCE_SAVE_VERSION_NAME,
                TimerConstants.PREFERENCE_SAVE_VERSION_INVALID
            )
        )
        assertNotSame(
            "",
            sharedPreferences.getString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, "")
        )

        multiTimerModel.addTimer()
        multiTimerModel.activeTimerSeconds = 10.0f

        val multiTimerModel2 = MultiTimerModel(get())

        val timers = multiTimerModel.timerList
        val timers2 = multiTimerModel2.timerList

        assertEquals(timers.size, timers2.size)

        for (index in 0 until timers.size) {
            assertEquals(timers[index].seconds.value, timers2[index].seconds.value)
            assertEquals(timers[index].defaultSeconds, timers2[index].defaultSeconds)
        }
    }

    /**
     * リスナーが未登録でないことを保証する.
     */
    @Test
    fun testListener() {
        val multiTimerModel = MultiTimerModel(get())

        multiTimerModel.timerList.forEach {
            assertNotNull(it.onEndListener)
        }
    }
}
