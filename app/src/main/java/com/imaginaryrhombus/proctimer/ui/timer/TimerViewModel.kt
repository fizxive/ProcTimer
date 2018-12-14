package com.imaginaryrhombus.proctimer.ui.timer

import android.arch.lifecycle.ViewModel

/**
 * タイマー用の ViewModel.
 * @param workingTimer 動作しているタイマー
 */
class TimerViewModel(var workingTimer : TimerModel = TimerModel(5 * 60.0f)) : ViewModel() {
}
