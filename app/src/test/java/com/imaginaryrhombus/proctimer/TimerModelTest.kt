package com.imaginaryrhombus.proctimer

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imaginaryrhombus.proctimer.ui.timer.TimerModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
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

        val timerModel = TimerModel()
        val tickMethod = timerModel.javaClass.getDeclaredMethod("tick", Float::class.java)
        tickMethod.isAccessible = true
        fun callTick(deltaSecond: Float) {
            tickMethod.invoke(timerModel, deltaSecond)
        }

        timerModel.setSeconds(60.0f)
        callTick(30.0f)
        assertEquals(timerModel.seconds.value, 30.0f)
        callTick(30.0f)
        assertEquals(timerModel.seconds.value, 0.0f)

        timerModel.setSeconds(15.0f)
        callTick(20.0f)
        assertEquals(timerModel.seconds.value, 0.0f)

        timerModel.setSeconds(50.0f)
        callTick(0.0f)
        assertEquals(timerModel.seconds.value, 50.0f)

        timerModel.setSeconds(0.0f)
        callTick(999.0f)
        assertEquals(timerModel.seconds.value, 0.0f)
    }
}
