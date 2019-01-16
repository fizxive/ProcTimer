package com.imaginaryrhombus.proctimer

import com.imaginaryrhombus.proctimer.ui.timer.TimerModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * テスト試行回数.
 */
const val TEST_TIMES : Int = 10

@RunWith(RobolectricTestRunner::class)
class TimerModelTest {

    /**
     * インスタンス作成テスト.
     */
    @Test
    fun testInitialize() {
        val timerModel = TimerModel()

        assertEquals(timerModel.seconds.value, 0.0f)
        assertEquals(timerModel.defaultSeconds, 0.0f)
        assertTrue(timerModel.isEnded)
        assertNull(timerModel.onEndListener)
    }

    /**
     * 秒数.設定テスト.
     */
    @Test
    fun setSecondsTest() {

        val timerModel = TimerModel()
        timerModel.setSeconds(5.0f)
        assertEquals(timerModel.seconds.value, 5.0f)
        assertEquals(timerModel.defaultSeconds, 5.0f)
        assertFalse(timerModel.isEnded)

        timerModel.setSeconds(99.0f)
        assertEquals(timerModel.seconds.value, 99.0f)
        assertEquals(timerModel.defaultSeconds, 99.0f)
        assertFalse(timerModel.isEnded)

        timerModel.setSeconds(0.0f)
        assertEquals(timerModel.seconds.value, 0.0f)
        assertEquals(timerModel.defaultSeconds, 0.0f)
        assertTrue(timerModel.isEnded)

        timerModel.setSeconds(-1.0f)
        assertEquals(timerModel.seconds.value, 0.0f)
        assertEquals(timerModel.defaultSeconds, 0.0f)
        assertTrue(timerModel.isEnded)
    }

    /**
     * 時間経過テスト
     */
    @Test
    fun testTick() {
        repeat(TEST_TIMES, fun (_: Int){
            val testSeconds = TimerModelTestUtility.getRandomFloat() / 2
            val testTickSeconds = TimerModelTestUtility.getRandomFloat() / 2

            val timerModel = TimerModel()
            timerModel.setSeconds(testSeconds)

            // private メソッドのテストになるのでリフレクションを使用してテスト.
            val tickMethod = timerModel.javaClass.getDeclaredMethod("tick", Float::class.java)
            tickMethod.isAccessible = true
            tickMethod.invoke(timerModel, testTickSeconds)

            val actualSeconds = if(testSeconds > testTickSeconds) testSeconds - testTickSeconds else 0.0f
            assertEquals(timerModel.seconds.value, actualSeconds)
            assertEquals(timerModel.isEnded, actualSeconds <= 0.0f)
        })
    }
}
