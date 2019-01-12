package com.imaginaryrhombus.proctimer.ui.timer

import android.content.Context
import android.os.Handler
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.imaginaryrhombus.proctimer.constants.TimerConstants
import java.util.concurrent.TimeUnit

/**
 * 一つ一つのタイマー用モデル.
 */
class TimerModel(context : Context) {

    /// タイマー終了時リスナー
    interface OnEndedListener {
        /**
         * タイマーが終了したときに呼ばれる.
         */
        fun onEnd()
    }

    /// 残り秒数.
    val seconds = MutableLiveData<Float>()

    /// 残り秒数(バッキングプロパティ).
    private var _seconds = 0.0f
    set(value) {
        field = value
        if (field < 0.0f) field = 0.0f
        seconds.postValue(field)
    }

    /// 初期秒数.
    private var defaultSeconds = 0.0f

    /// 現在のタイマーが終了しているか.
    val isEnded : Boolean
    get() = _seconds <= 0.0f

    /// 終了時のコールバック
    var onEndListener : OnEndedListener? = null

    /// ローカルデータ読み書き用.
    private val sharedPreferences = context.getSharedPreferences(TimerConstants.PREFERENCE_NAME, Context.MODE_PRIVATE)

    init {
        _seconds = sharedPreferences?.getFloat(TimerConstants.PREFERENCE_PARAM_SEC_NAME, TimerConstants.TIMER_DEFAULT_SECONDS)
            ?: TimerConstants.TIMER_DEFAULT_SECONDS
        defaultSeconds = _seconds
    }

    /**
     * 秒数を設定する.
     */
    fun setSeconds(seconds: Float) {
        if (isTicking) stopTick()
        _seconds = seconds
        sharedPreferences?.edit()?.putFloat("seconds", _seconds)?.apply()
        defaultSeconds = _seconds
    }

    /**
     * 時間を経過させる.
     * @param deltaSeconds 経過時間.
     */
    private fun tick(deltaSeconds :Float) {
        _seconds -= deltaSeconds
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
                onEndListener?.onEnd()
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

    fun reset() {
        if (isTicking) stopTick()
        _seconds = defaultSeconds
    }
}
