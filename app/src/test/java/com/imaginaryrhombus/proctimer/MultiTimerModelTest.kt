package com.imaginaryrhombus.proctimer

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.FirebaseApp
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import com.imaginaryrhombus.proctimer.ui.timer.MultiTimerModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class MultiTimerModelTest {

    init {
        // JvmStatic, BeforeClass だとランタイムエラーを起こす.
        FirebaseApp.initializeApp(ApplicationProvider.getApplicationContext<Application>())
    }

    /**
     * Activity 生成のための Rule.
     */
    @JvmField @Rule
    val scenarioRule = ActivityScenarioRule(TimerActivity::class.java)

    private fun getSharedPreferences(activity: Activity): SharedPreferences {
        return activity.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
    }

    private fun clearSharedPreferences(sharedPreferences: SharedPreferences): SharedPreferences {
        return sharedPreferences.apply {
            edit().clear().apply()
        }
    }

    /**
     * まっさらな状態からインスタンスを生成したときの動作を確認する.
     */
    @Test
    fun testCreate() {
        scenarioRule.scenario.onActivity {
            clearSharedPreferences(getSharedPreferences(it))

            val multiTimerModel = MultiTimerModel(it)

            assertEquals(multiTimerModel.activeTimerSeconds, TimerConstants.TIMER_DEFAULT_SECONDS)

            val timers = multiTimerModel.timerList
            assertEquals(timers.size, TimerConstants.TIMER_DEFAULT_COUNTS)

            timers.forEach {
                assertEquals(it.seconds.value, TimerConstants.TIMER_DEFAULT_SECONDS)
                assertEquals(it.defaultSeconds, TimerConstants.TIMER_DEFAULT_SECONDS)
            }
        }
    }

    /**
     * タイマー取得の挙動を確認する.
     */
    @Test
    fun testGetTimer() {
        scenarioRule.scenario.onActivity {
            clearSharedPreferences(getSharedPreferences(it))

            val multiTimerModel = MultiTimerModel(it)
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
                assertEquals(size, 3)
                assertEquals(get(0).seconds.value, 30.0f)
                assertEquals(get(1).seconds.value, 60.0f)
                assertEquals(get(2).seconds.value, 90.0f)
                assertEquals(get(0).defaultSeconds, 30.0f)
                assertEquals(get(1).defaultSeconds, 60.0f)
                assertEquals(get(2).defaultSeconds, 90.0f)
            }
        }
    }

    /**
     * SharedPreferences への保存/読み出しが正常か確認する.
     */
    @Test
    fun testSaveAndRestore() {
        scenarioRule.scenario.onActivity {
            val sharedPreferences = clearSharedPreferences(getSharedPreferences(it))

            val multiTimerModel = MultiTimerModel(it)

            assertTrue(sharedPreferences.contains(TimerConstants.PREFERENCE_SAVE_VERSION_NAME))
            assertEquals(
                sharedPreferences.getInt(
                    TimerConstants.PREFERENCE_SAVE_VERSION_NAME,
                    TimerConstants.PREFERENCE_SAVE_VERSION_INVALID
                ),
                TimerConstants.PREFERENCE_SAVE_VERSION
            )
            assertNotSame(
                sharedPreferences.getString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, ""),
                ""
            )

            multiTimerModel.addTimer()
            multiTimerModel.activeTimerSeconds = 10.0f

            val multiTimerModel2 = MultiTimerModel(it)

            val timers = multiTimerModel.timerList
            val timers2 = multiTimerModel2.timerList

            assertEquals(timers.size, timers2.size)

            for (index in 0 until timers.size) {
                assertEquals(timers[index].seconds.value, timers2[index].seconds.value)
                assertEquals(timers[index].defaultSeconds, timers2[index].defaultSeconds)
            }
        }
    }
}
