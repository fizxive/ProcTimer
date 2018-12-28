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
            val tickMethod = timerModel.javaClass.getDeclaredMethod("tick", Float::class.java)
            tickMethod.isAccessible = true
            tickMethod.invoke(timerModel, testTickSeconds)

            val actualSeconds = if(testSeconds > testTickSeconds) testSeconds - testTickSeconds else 0.0f
            assertEquals(actualSeconds, timerModel.seconds.value)
        })
    }

    /// 秒数が正常にフォーマットされているか.
    @Test
    fun testText() {

        fun testImpl(seconds : Float, timerText : String) {
            TimerModelTestUtility.createTimerModel(seconds, testActivity).run {
                assertEquals(text.value, timerText)
            }
        }

        testImpl(30.0f, "00:30.000")
        testImpl(60.0f, "01:00.000")
        testImpl(75.0f, "01:15.000")
        testImpl(0.0f, "00:00.000")
        testImpl(-10.0f, "00:00.000")
        testImpl(10.110f, "00:10.110")
        testImpl(60.085f, "01:00.085")
        testImpl(66.666f, "01:06.666")
    }
}
