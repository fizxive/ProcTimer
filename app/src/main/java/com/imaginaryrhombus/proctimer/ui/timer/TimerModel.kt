package com.imaginaryrhombus.proctimer.ui.timer

import android.os.Handler
import androidx.lifecycle.MutableLiveData

/**
 * 一つ一つのタイマー用モデル.
 */
class TimerModel{

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
        seconds.value = field
    }

    /// 初期秒数.
    var defaultSeconds = 0.0f
    private set

    /// 現在のタイマーが終了しているか.
    val isEnded : Boolean
    get() = _seconds <= 0.0f

    /// 終了時のコールバック
    var onEndListener : OnEndedListener? = null

    /**
     * 秒数を設定する.
     */
    fun setSeconds(seconds: Float) {
        if (isTicking) stopTick()
        _seconds = seconds
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

    /// 時間経過制御用クラス.
    private var timeTicker = TimeTicker()

    /// 動作中かのフラグ.
    private var isTicking = false

    /// 時間経過用のワーカー.
    private val tickRunner = object : Runnable {

        override fun run() {

            timeTicker.saveTick()
            tick(timeTicker.latestTick)

            if (isEnded) {
                onEndListener?.onEnd()
            }
            else {
                tickHandler.postDelayed(this, tickInterval)
            }
        }
    }

    /**
     * 時間計測を開始.
     */
    fun startTick() {
        timeTicker.resetTickSeconds()
        if (isTicking.not()) {
            tickHandler.post(tickRunner)
            isTicking = true
        }
    }

    /**
     * 動作している時間計測を停止する.
     */
    fun stopTick() {
        if (isTicking) {
            tickHandler.removeCallbacks(tickRunner)
            isTicking = false
        }
    }

    /**
     * 経過時間をリセット.
     */
    fun reset() {
        if (isTicking) stopTick()
        _seconds = defaultSeconds
    }
}
