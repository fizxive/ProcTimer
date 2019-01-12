package com.imaginaryrhombus.proctimer

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

/// テスト試行回数.
const val TEST_TIMES : Int = 10

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
            assertEquals(testSeconds, timerModel.seconds.value)
            assertEquals(timerModel.isEnded, testSeconds <= 0.0f)
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

            /// private メソッドのテストになるのでリフレクションを使用してテスト.
            val tickMethod = timerModel.javaClass.getDeclaredMethod("saveTick", Float::class.java)
            tickMethod.isAccessible = true
            tickMethod.invoke(timerModel, testTickSeconds)

            val actualSeconds = if(testSeconds > testTickSeconds) testSeconds - testTickSeconds else 0.0f
            assertEquals(timerModel.seconds.value, actualSeconds)
            assertEquals(timerModel.isEnded, actualSeconds <= 0.0f)
        })
    }

    @Test
    fun setSecondsTest() {

        val model = TimerModelTestUtility.createTimerModel(5.0f, testActivity)
        assertEquals(model.seconds.value, 5.0f)

        model.setSeconds(99.0f)
        assertEquals(model.seconds.value, 99.0f)
        assertFalse(model.isEnded)

        model.setSeconds(0.0f)
        assertEquals(model.seconds.value, 0.0f)
        assertTrue(model.isEnded)

        model.setSeconds(-1.0f)
        assertEquals(model.seconds.value, 0.0f)
        assertTrue(model.isEnded)
    }
}
