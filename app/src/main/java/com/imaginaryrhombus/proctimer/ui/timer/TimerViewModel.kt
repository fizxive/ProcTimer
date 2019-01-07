package com.imaginaryrhombus.proctimer.ui.timer

import android.app.Application
import androidx.lifecycle.*
import java.util.concurrent.TimeUnit

/**
 * タイマー用の ViewModel.
 */
class TimerViewModel(app : Application) : AndroidViewModel(app) {

    /// タイマー本体.(外部からの直接設定をしないようにする)
    val timer : TimerModel
    get() {
        return multiTimerModel.activeTimerModel
    }

    private var multiTimerModel = MultiTimerModel(app.applicationContext)

    /**
     * タイマーのスタートを Model に伝える.
     */
    fun startTick() {
        timer.startTick()
    }

    /**
     * タイマーの停止を Model に伝える.
     */
    fun stopTick() {
        timer.stopTick()
    }

    /**
     * タイマーをリセットする.
     */
    fun resetTimer() {
        timer.reset()
    }

    /**
     * タイマーを次のものにする.
     */
    fun nextTimer() {
        multiTimerModel.next()
    }

    /**
     * テキスト情報からタイマーを設定する.
     */
    fun setTimerFrom(minutes: String, seconds: String) {
        val minutesLong = minutes.toLong()
        val secondsLong = seconds.toLong()

        timer.setSeconds((TimeUnit.MINUTES.toSeconds(minutesLong) + secondsLong).toFloat())
    }

    /**
     * タイマー情報からテキスト情報に変換する.
     * @return 分、秒の順番で格納された文字列.
     */
    fun toTimerString() : Pair<String, String> {
        val secondsLong = timer.seconds.value!!.toLong()
        return Pair((secondsLong / 60).toString(), (secondsLong % 60).toString())
    }

    fun setTimerEndListener(listener: TimerModel.OnEndedListener?) {
        timer.onEndedListener = listener
    }
}
