package com.imaginaryrhombus.proctimer.ui.timer

import android.app.Application
import androidx.lifecycle.*
import kotlin.math.max
import java.util.concurrent.TimeUnit
import com.imaginaryrhombus.proctimer.R

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

    /// 現在のタイマーのテキスト.
    val currentTimerText : LiveData<String> = Transformations.map(timer.seconds) {
        createTimerStringFromSeconds(it)
    }

    /**
     * 準備中のタイマーをテキスト表示する数.
     * TODO : ビュー側から設定するようにしたい.
     */
    private val displayNextTimerCount = 3

    /// 準備中のタイマーのテキスト.
    val nextTimerStrings = MutableList (displayNextTimerCount) { MutableLiveData<String>() }

    private val timerChangedListener = object : MultiTimerModel.OnTimerChangedListener {
        override fun onTimerChanged() {
            // 予めタイマーが無い文字列で初期化してからタイマーを取得して文字列を更新する.
            nextTimerStrings.forEach {
                it.postValue(app.applicationContext.getString(R.string.timer_invalid_text))
            }

            val timers = multiTimerModel.getTimers(nextTimerStrings.size)
            nextTimerStrings.forEachIndexed { index, timerText ->
                timers[index]?.let {
                    timerText.postValue(createTimerStringFromSeconds(it.seconds.value!!))
                }
            }
        }
    }

    init {
        // 初期化子で設定を行うと 'Type checking has run into a recursive problem.' が発生してしまうのでここで行う.
        multiTimerModel.onTimerChangedListener = timerChangedListener
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

    /**
     * それぞれのタイマー終了時の動作を設定する.
     * @param listener リスナー本体.(null を設定すると 解除)
     */
    fun setTimerEndListener(listener: TimerModel.OnEndedListener?) {
        multiTimerModel.onEachTimerEndedListener = listener
    }

    private companion object {
        /**
         * 秒数からフォーマットした文字列に変更.
         * @param inputSeconds 秒数.
         * @note 秒数が負の値の場合は0秒として扱う.
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
