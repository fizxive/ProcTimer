package com.imaginaryrhombus.proctimer.ui.timer

import android.content.Context
import android.os.Handler
import androidx.lifecycle.MutableLiveData
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import java.io.Closeable
import java.util.concurrent.TimeUnit

/**
 * 一つ一つのタイマー用モデル.
 */
class TimerModel(context : Context) : Closeable {

    /// 残り秒数.
    var seconds = MutableLiveData<Float>()
    private set

    /// 残り秒数(バッキングプロパティ).
    private var _seconds = 0.0f
    set(value) {
        field = value
        if (field < 0.0f) field = 0.0f
        seconds.postValue(field)
        updateText()
    }

    /// このタイマーをテキスト化したときの表示.
    var text = MutableLiveData<String>()
    private set

    /// 現在のタイマーが終了しているか.
    val isEnded : Boolean
    get() = _seconds <= 0.0f

    /// ローカルデータ読み書き用.
    private val sharedPreferences = context.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    init {
        _seconds = sharedPreferences?.getFloat(TimerConstants.PREFERENCE_PARAM_SEC_NAME, TimerConstants.TIMER_DEFAULT_SECONDS)
            ?: TimerConstants.TIMER_DEFAULT_SECONDS
    }

    override fun close() {
        sharedPreferences?.edit()?.putFloat("seconds", _seconds)?.apply()
    }

    /**
     * 時間を経過させる.
     * @param deltaSeconds 経過時間.
     */
    private fun tick(deltaSeconds :Float) {
        _seconds -= deltaSeconds
    }

    /**
     * 秒数に合わせてテキストを更新する.
     */
    private fun updateText() {
        val timerMilliseconds = _seconds.times(1000.0f).toLong()
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timerMilliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timerMilliseconds - TimeUnit.MINUTES.toMillis(minutes))
        val milliseconds = timerMilliseconds - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds)
        text.value = ("%02d:%02d.%03d".format(minutes, seconds, milliseconds))
    }

    /// 時間経過の判定を行う間隔.
    private val tickInterval = 10L

    /// 時間経過用のハンドラ.
    private val tickHandler = Handler()
    private var timeTicker = TimeTicker()
    private var isTicking = false

    /// 時間経過用のワーカー.
    private val tickRunner = object : Runnable {

        override fun run() {

            timeTicker.tick()
            tick(timeTicker.latestTick)

            if (isEnded) {
                /// TODO : ダイアログを表示するなどしたい. Fragment に委ねる?
            }
            else {
                tickHandler.postDelayed(this, tickInterval)
            }
        }
    }

    fun startTick() {
        timeTicker.setPrevious()
        if (isTicking.not()) {
            tickHandler.post(tickRunner)
            isTicking = true
        }
    }

    fun stopTick() {
        if (isTicking) {
            tickHandler.removeCallbacks(tickRunner)
            isTicking = false
        }
    }
}
