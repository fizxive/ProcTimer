package com.imaginaryrhombus.proctimer.ui.timer

import java.util.*

/**
 * 時間経過を司る内部クラス
 */
class TimeTicker {

    private var prevMilliseconds = 0L

    var latestTick = 0.0f
        private set

    init {
        setPrevious()
    }

    fun tick() {
        val currentMilliseconds = Calendar.getInstance(Locale.getDefault()).timeInMillis
        val deltaMilliseconds = currentMilliseconds - prevMilliseconds

        latestTick = deltaMilliseconds / 1000.0f
        prevMilliseconds = currentMilliseconds

        setPrevious()
    }

    fun setPrevious() {
        prevMilliseconds = Calendar.getInstance(Locale.getDefault()).timeInMillis
    }
}