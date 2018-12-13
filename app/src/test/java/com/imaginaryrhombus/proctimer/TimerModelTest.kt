package com.imaginaryrhombus.proctimer

import com.imaginaryrhombus.proctimer.ui.timer.TimerModel
import org.junit.Test
import org.junit.Assert.*

class TimerModelTest {

    private val testTimes = 50

    /// 秒数設定のための無作為な少数を生成する.
    fun getRandomFloat() :Float {return Math.random().toFloat() * Float.MAX_VALUE}

    /**
     * 秒設定テスト
     */
    @Test
    fun second_correct() {
        repeat(testTimes, fun (_: Int) {
            val testSeconds = getRandomFloat()
            var timerModel = TimerModel(testSeconds)
            assertEquals(timerModel.seconds, testSeconds)
        })
    }

    /**
     * 時間経過テスト
     */
    @Test
    fun tick_correct() {
        repeat(testTimes, fun (_: Int){
            val testSeconds = getRandomFloat() / 2
            val testTickSeconds = getRandomFloat() / 2
            var timerModel = TimerModel(testSeconds)

            timerModel.tick(testTickSeconds)

            val actualSeconds = if(testSeconds > testTickSeconds) testSeconds - testTickSeconds else 0.0f
            assertEquals(timerModel.seconds, actualSeconds)
        })
    }
}