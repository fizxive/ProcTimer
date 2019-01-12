package com.imaginaryrhombus.proctimer.ui.timer

import java.util.*

/**
 * 時間経過を司る内部クラス
 */
class TimeTicker {

    private var prevMilliseconds = 0L

    /**
     * saveTick() もしくは resetTickSeconds() してからの経過時間.
     * @note saveTick() を呼び出してから出ないと値が正しくないので注意.
     */
    var latestTick = 0.0f
        private set

    init {
        resetTickSeconds()
    }

    /**
     * 経過時間を latestTick に保存する.
     */
    fun saveTick() {
        val currentMilliseconds = Calendar.getInstance(Locale.getDefault()).timeInMillis
        val deltaMilliseconds = currentMilliseconds - prevMilliseconds

        latestTick = deltaMilliseconds / 1000.0f
        prevMilliseconds = currentMilliseconds

        resetTickSeconds()
    }

    /**
     * 時間経過をリセットする.
     */
    fun resetTickSeconds() {
        prevMilliseconds = Calendar.getInstance(Locale.getDefault()).timeInMillis
    }
}
