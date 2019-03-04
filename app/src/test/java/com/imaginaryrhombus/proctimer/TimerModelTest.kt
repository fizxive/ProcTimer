package com.imaginaryrhombus.proctimer

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.imaginaryrhombus.proctimer.ui.timer.TimerModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.AutoCloseKoinTest

@RunWith(AndroidJUnit4::class)
class TimerModelTest : AutoCloseKoinTest() {

    /**
     * インスタンス作成テスト.
     */
    @Test
    fun testInitialize() {
        val timerModel = TimerModel()

        assertEquals(0.0f, timerModel.seconds.value)
        assertEquals(0.0f, timerModel.defaultSeconds)
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
        assertEquals(5.0f, timerModel.seconds.value)
        assertEquals(5.0f, timerModel.defaultSeconds)
        assertFalse(timerModel.isEnded)

        timerModel.setSeconds(99.0f)
        assertEquals(99.0f, timerModel.seconds.value)
        assertEquals(99.0f, timerModel.defaultSeconds)
        assertFalse(timerModel.isEnded)

        timerModel.setSeconds(0.0f)
        assertEquals(0.0f, timerModel.seconds.value)
        assertEquals(0.0f, timerModel.defaultSeconds)
        assertTrue(timerModel.isEnded)

        timerModel.setSeconds(-1.0f)
        assertEquals(0.0f, timerModel.seconds.value)
        assertEquals(0.0f, timerModel.defaultSeconds)
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
        assertEquals(30.0f, timerModel.seconds.value)
        callTick(30.0f)
        assertEquals(0.0f, timerModel.seconds.value)

        timerModel.setSeconds(15.0f)
        callTick(20.0f)
        assertEquals(0.0f, timerModel.seconds.value)

        timerModel.setSeconds(50.0f)
        callTick(0.0f)
        assertEquals(50.0f, timerModel.seconds.value)

        timerModel.setSeconds(0.0f)
        callTick(999.0f)
        assertEquals(0.0f, timerModel.seconds.value)
    }
}
