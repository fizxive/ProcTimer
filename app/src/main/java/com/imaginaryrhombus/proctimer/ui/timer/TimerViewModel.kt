package com.imaginaryrhombus.proctimer.ui.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import kotlin.math.max
import kotlin.math.ceil
import java.util.concurrent.TimeUnit
import com.imaginaryrhombus.proctimer.R

/**
 * タイマー用の ViewModel.
 * @param app アプリケーション.(ViewModelProvider を使用する限り、特に意識する必要はない.)
 */
class TimerViewModel(private val app: Application) : AndroidViewModel(app) {

    /**
     * 複数のタイマーを管理するタイマー本体.
     */
    private var multiTimerModel = MultiTimerModel(app.applicationContext)

    /**
     * 準備中のタイマーをテキスト表示する数.
     * TODO : ビュー側から設定するようにしたい.
     */
    private val displayNextTimerCount = 3

    /**
     * 現在のタイマーのテキスト.
     */
    val currentTimerText: LiveData<String>

    /**
     * 準備中のタイマーのテキスト.
     */
    private val _nextTimerStrings = MutableList(displayNextTimerCount) { MutableLiveData<String>() }

    val nextTimerStrings: List<MutableLiveData<String>>
    get() {
        return _nextTimerStrings.toList()
    }

    /**
     * タイマーが変更されたときのリスナー.
     */
    private val timerChangedListener = object : MultiTimerModel.OnTimerChangedListener {
        override fun onTimerChanged() {
            updateNextTimerText()
        }

        override fun onTimerAdded() {
            updateNextTimerText()
        }

        override fun onTimerRemoved() {
            updateNextTimerText()
        }
    }

    init {
        // 初期化子で設定を行うと 'Type checking has run into a recursive problem.' が発生してしまうのでここで行う.
        multiTimerModel.onTimerChangedListener = timerChangedListener
        // テキストが空白なので、更新する
        updateNextTimerText()

        currentTimerText = Transformations.switchMap(multiTimerModel.activeTimerModel) {
                timerModel ->
            Transformations.map(timerModel.seconds) { seconds ->
                createTimerStringFromSeconds(seconds)
            }
        }
    }

    /**
     * タイマーのスタートを Model に伝える.
     */
    fun startTick() {
        multiTimerModel.startCurrentTimer()
    }

    /**
     * タイマーの停止を Model に伝える.
     */
    fun stopTick() {
        multiTimerModel.stopCurrentTimer()
    }

    /**
     * タイマーの追加を Model に伝える.
     */
    fun addTimer(onFailureListener: () -> Unit = {}) {
        multiTimerModel.addTimer(onFailureListener)
    }

    /**
     * タイマーの削除を Model に伝える.
     * @param onFailureListener タイマーの削除に失敗したときの動作.
     */
    fun removeTimer(onFailureListener: () -> Unit = {}) {
        multiTimerModel.removeCurrentTimer(onFailureListener)
    }

    /**
     * タイマーをリセットする.
     */
    fun resetTimer() {
        multiTimerModel.resetCurrentTimer()
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
    fun setCurrentTimerFrom(minutes: String, seconds: String) {
        val minutesLong = minutes.toLong()
        val secondsLong = seconds.toLong()

        multiTimerModel.activeTimerSeconds =
            (TimeUnit.MINUTES.toSeconds(minutesLong) + secondsLong).toFloat()
    }

    /**
     * タイマー情報からテキスト情報に変換する.
     * @return 分、秒の順番で格納された文字列.
     */
    fun toTimerString(): Pair<String, String> {
        val secondsLong =
            checkNotNull(multiTimerModel.activeTimerSeconds) {
                "This timer is not initialized"
            }.toLong()
        return Pair((secondsLong / 60).toString(), (secondsLong % 60).toString())
    }

    /**
     * それぞれのタイマー終了時の動作を設定する.
     * @param listener リスナー本体.(null を設定すると 解除)
     */
    fun setTimerEndListener(listener: TimerModel.OnEndedListener?) {
        multiTimerModel.onEachTimerEndedListener = listener
    }

    /**
     * 次のタイマーの表示を更新する.
     */
    private fun updateNextTimerText() {
        // 予めタイマーが無い文字列で初期化してからタイマーを取得して文字列を更新する.
        _nextTimerStrings.forEach {
            it.postValue(app.applicationContext.getString(R.string.timer_invalid_text))
        }

        val timers = multiTimerModel.timerList
        var index = 0
        timers.forEach { timerText ->
            if (timerText != timers.first()) {
                timerText.also {
                    _nextTimerStrings[index++].postValue(
                        createTimerStringFromSeconds(
                            checkNotNull(it.seconds.value) { "This timer is not initialized" })
                    )
                }
            }
        }
    }

    private companion object {
        /**
         * 秒数からフォーマットした文字列に変更.
         * @param inputSeconds 秒数.
         * @note 秒数が負の値の場合は0秒として扱う.
         */
        @JvmStatic
        fun createTimerStringFromSeconds(inputSeconds: Float): String {
            val timerSeconds = max(ceil(inputSeconds).toLong(), 0)
            val minutes = TimeUnit.SECONDS.toMinutes(timerSeconds)
            val seconds = TimeUnit.SECONDS.toSeconds(
                timerSeconds - TimeUnit.MINUTES.toSeconds(minutes))
            return ("%02d:%02d".format(minutes, seconds))
        }
    }
}
