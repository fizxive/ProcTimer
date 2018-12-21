package com.imaginaryrhombus.proctimer.ui.timer

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Handler

/**
 * タイマー用の ViewModel.
 */
class TimerViewModel(app : Application) : AndroidViewModel(app) {

    /// タイマーのテキスト化したときの表記.
    var timerText = MutableLiveData<String>()
        private set

    /// タイマー本体.(外部からの直接設定をしないようにする)
    private val timer = TimerModel(app.applicationContext)

    /// 時間経過の判定を行う間隔.
    private val tickInterval = 10L

    /// 時間経過用のハンドラ.
    private val tickHandler = Handler()

    private var timeTicker = TimeTicker()

    /// 時間経過用のワーカー.
    private val tickRunner = object : Runnable {

        override fun run() {

            timeTicker.tick()

            timer.tick(timeTicker.latestTick)

            updateText()

            tickHandler.postDelayed(this, tickInterval)
        }
    }

    init {
        startTick()
    }

    fun startTick() {
        timeTicker.setPrevious()
        tickHandler.post(tickRunner)
    }

    fun stopTick() {
        tickHandler.removeCallbacks(tickRunner)
    }

    private fun updateText() {
        val timerSecondsInt = timer.seconds.toInt()
        val minutes = timerSecondsInt / 60
        val seconds = timerSecondsInt % 60
        timerText.postValue("%02d:%02d".format(minutes, seconds))
    }
}
