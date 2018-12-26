package com.imaginaryrhombus.proctimer

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
    /// MEMO : ActivityController は今は不要なので buildActivity() ではなく setupActivity() を使用する.
    private val testActivity = Robolectric.setupActivity(TimerActivity::class.java)!!

    /**
     * 秒設定テスト
     */
    @Test
    fun testInitialize() {
        repeat(TEST_TIMES, fun (_: Int) {
            val testSeconds = TimerModelTestUtility.getRandomFloat()
            val timerModel = TimerModelTestUtility.createTimerModel(testSeconds, testActivity)
            assertEquals(testSeconds, timerModel.seconds)
        })
    }

    /**
     * 時間経過テスト
     */
    @Test
    fun testTick() {
        repeat(TEST_TIMES, fun (_: Int){
            val testSeconds = TimerModelTestUtility.getRandomFloat() / 2
            val testTickSeconds = TimerModelTestUtility.getRandomFloat() / 2
            val timerModel = TimerModelTestUtility.createTimerModel(testSeconds, testActivity)

            timerModel.tick(testTickSeconds)

            val actualSeconds = if(testSeconds > testTickSeconds) testSeconds - testTickSeconds else 0.0f
            assertEquals(actualSeconds, timerModel.seconds)
        })
    }
}
