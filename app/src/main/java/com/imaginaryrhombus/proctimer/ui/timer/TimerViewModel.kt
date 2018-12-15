package com.imaginaryrhombus.proctimer.ui.timer

import android.arch.lifecycle.ViewModel
import android.databinding.ObservableField

/**
 * タイマー用の ViewModel.
 * @param workingTimer 動作しているタイマー
 */
class TimerViewModel(private var workingTimer : TimerModel) : ViewModel() {

    var timerText = ObservableField<String>(workingTimer.text)

}
