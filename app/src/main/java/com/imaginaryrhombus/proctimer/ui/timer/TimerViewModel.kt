package com.imaginaryrhombus.proctimer.ui.timer

import android.arch.lifecycle.ViewModel

class TimerViewModel : ViewModel() {

    /// 実行中タイマー
    var primaryTimer = TimerModel(5 * 60.0f)
}
