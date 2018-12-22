package com.imaginaryrhombus.proctimer

import android.content.Context
import com.imaginaryrhombus.proctimer.ui.timer.TimerModel
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner


/// テスト試行回数.
const val TEST_TIMES : Int = 50

@RunWith(RobolectricTestRunner::class)
class TimerModelTest {

    /// テスト用の Activity.
    private val testActivity = Robolectric.setupActivity(TimerActivity::class.java)!!

    /// 秒数設定のための無作為な少数を生成する.
    private fun getRandomFloat() : Float {return Math.random().toFloat() * Float.MAX_VALUE}

    /** テスト用の TimerModel を作成する.
     * @param seconds 設定する秒数.
     * @see TimerModel
     */
    private fun createTimerModel(seconds : Float) : TimerModel {
        val context = testActivity.applicationContext
        context.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)
            .edit().putFloat(TimerConstants.PREFERENCE_PARAM_SEC_NAME, seconds).apply()
        return TimerModel(context)
    }

    /**
     * 秒設定テスト
     */
    @Test
    fun second_correct() {
        repeat(TEST_TIMES, fun (_: Int) {
            val testSeconds = getRandomFloat()
            val timerModel = createTimerModel(testSeconds)
            assertEquals(timerModel.seconds, testSeconds)
        })
    }

    /**
     * 時間経過テスト
     */
    @Test
    fun tick_correct() {
        repeat(TEST_TIMES, fun (_: Int){
            val testSeconds = getRandomFloat() / 2
            val testTickSeconds = getRandomFloat() / 2
            val timerModel = createTimerModel(testSeconds)

            timerModel.tick(testTickSeconds)

            val actualSeconds = if(testSeconds > testTickSeconds) testSeconds - testTickSeconds else 0.0f
            assertEquals(timerModel.seconds, actualSeconds)
        })
    }
}