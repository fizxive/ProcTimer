package com.imaginaryrhombus.proctimer

import android.content.SharedPreferences
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.imaginaryrhombus.proctimer.application.TimerComponent
import com.imaginaryrhombus.proctimer.application.TimerSharedPreferencesComponent
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import com.imaginaryrhombus.proctimer.ui.timer.MultiTimerModel
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
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

    /**
     * SharedComponent　を使うためのクラス.
     * テストごとにモックの必要有無が違うのでアノテーションで inject しないようにする.
     */
    private lateinit var sharedPreferencesComponent: TimerSharedPreferencesComponent

    /**
     * まっさらな状態からインスタンスを生成したときの動作を確認する.
     */
    @Test
    fun testCreate() {
        sharedPreferencesComponent = mockk()

        every {
            sharedPreferencesComponent.timerSecondsList
        } returns listOf<Float>(
            TimerConstants.TIMER_DEFAULT_SECONDS, TimerConstants.TIMER_DEFAULT_SECONDS
        )

        val multiTimerModel = MultiTimerModel(sharedPreferencesComponent)

        assertEquals(TimerConstants.TIMER_DEFAULT_SECONDS, multiTimerModel.activeTimerSeconds)

        val timers = multiTimerModel.timerList
        assertEquals(TimerConstants.TIMER_DEFAULT_COUNTS, timers.size)

        timers.forEach {
            assertEquals(TimerConstants.TIMER_DEFAULT_SECONDS, it.seconds.value)
            assertEquals(TimerConstants.TIMER_DEFAULT_SECONDS, it.defaultSeconds)
        }

        verify(exactly = 1) {
            sharedPreferencesComponent.timerSecondsList
        }
    }

    /**
     * タイマー取得の挙動を確認する.
     */
    @Test
    fun testGetTimer() {
        sharedPreferencesComponent = mockk()

        // timerSecondsList は Mutable ではないので slot<List<Float>> を使用してキャプチャする
        val timerListCapture = slot<List<Float>>()

        every {
            sharedPreferencesComponent.timerSecondsList = capture(timerListCapture)
        } just runs

        every {
            sharedPreferencesComponent.timerSecondsList
        } answers {
            timerListCapture.captured
        }

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
        val mockSharedPreferences = mockk<SharedPreferences>()
        val mockSharedPreferencesEditor = mockk<SharedPreferences.Editor>()
        val timerComponent = mockk<TimerComponent>(relaxUnitFun = true)
        val timerSlot = slot<String>()

        every {
            timerComponent.sharedPreferences
        } returns mockSharedPreferences

        every {
            mockSharedPreferences.getInt(TimerConstants.PREFERENCE_SAVE_VERSION_NAME, any())
        } returns TimerConstants.PREFERENCE_SAVE_VERSION_INVALID
        every {
            mockSharedPreferences.getString(TimerConstants.PREFERENCE_PARAM_SEC_NAME, any())
        } answers { timerSlot.captured } // 　遅延評価が必要なので answers で返す
        every {
            mockSharedPreferences.edit()
        } returns mockSharedPreferencesEditor

        every {
            mockSharedPreferencesEditor.putString(
                TimerConstants.PREFERENCE_PARAM_SEC_NAME, capture(timerSlot)
            )
        } returns mockSharedPreferencesEditor
        every {
            mockSharedPreferencesEditor.putInt(TimerConstants.TIMER_THEME_NAME, any())
        } returns mockSharedPreferencesEditor
        every {
            mockSharedPreferencesEditor.putInt(TimerConstants.PREFERENCE_SAVE_VERSION_NAME, any())
        } returns mockSharedPreferencesEditor
        every {
            mockSharedPreferencesEditor.apply()
        } just runs

        sharedPreferencesComponent = spyk(TimerSharedPreferencesComponent(timerComponent))

        sharedPreferencesComponent.reset()

        val gson = Gson()

        verify {
            mockSharedPreferencesEditor.putInt(
                TimerConstants.PREFERENCE_SAVE_VERSION_NAME, TimerConstants.PREFERENCE_SAVE_VERSION
            )
            mockSharedPreferencesEditor.putInt(
                TimerConstants.TIMER_THEME_NAME, TimerConstants.TIMER_THEME_DEFAULT
            )
            mockSharedPreferencesEditor.putString(
                TimerConstants.PREFERENCE_PARAM_SEC_NAME,
                gson.toJson(
                    List(TimerConstants.TIMER_DEFAULT_COUNTS) {
                        TimerConstants.TIMER_DEFAULT_SECONDS
                    }
                )
            )
        }

        val multiTimerModel = MultiTimerModel(get())

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
        sharedPreferencesComponent = mockk()

        every {
            sharedPreferencesComponent.timerSecondsList
        } returns listOf<Float>(
            TimerConstants.TIMER_DEFAULT_SECONDS, TimerConstants.TIMER_DEFAULT_SECONDS
        )

        val multiTimerModel = MultiTimerModel(get())

        multiTimerModel.timerList.forEach {
            assertNotNull(it.onEndListener)
        }
    }
}
