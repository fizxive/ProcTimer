package com.imaginaryrhombus.proctimer.ui.timer

import android.app.Application
import androidx.lifecycle.*
import kotlin.math.max
import java.util.concurrent.TimeUnit

/**
 * タイマー用の ViewModel.
 */
class TimerViewModel(app : Application) : AndroidViewModel(app) {

    /// 複数のタイマーを管理するタイマー本体.
    private var multiTimerModel = MultiTimerModel(app.applicationContext)

    /// タイマー本体からの動作中のタイマーの参照.
    val timer : TimerModel
    get() {
        return multiTimerModel.activeTimerModel
    }

    val currentTimerText : LiveData<String> = Transformations.map(timer.seconds) {
        createTimerStringFromSeconds(it)
    }

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

    private companion object {
        /**
         * 秒数からフォーマットした文字列に変更.
         * @param inputSeconds 秒数.
         * @note 秒数が負の値の場合は0病として扱う.
         */
        @JvmStatic
        fun createTimerStringFromSeconds(inputSeconds: Float): String {
            val timerMilliseconds = max(inputSeconds.times(1000.0f).toLong(), 0)
            val minutes = TimeUnit.MILLISECONDS.toMinutes(timerMilliseconds)
            val seconds = TimeUnit.MILLISECONDS.toSeconds(timerMilliseconds - TimeUnit.MINUTES.toMillis(minutes))
            val milliseconds = timerMilliseconds - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds)
            return ("%02d:%02d.%03d".format(minutes, seconds, milliseconds))
        }
    }
}
