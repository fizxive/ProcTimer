package com.imaginaryrhombus.proctimer.ui.timer

import android.app.Application
import androidx.lifecycle.*

/**
 * タイマー用の ViewModel.
 */
class TimerViewModel(val app : Application) : AndroidViewModel(app) {

    /// タイマー本体.(外部からの直接設定をしないようにする)
    var timer = TimerModel(app.applicationContext)
    private set

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
}
