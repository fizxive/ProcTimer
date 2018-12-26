package com.imaginaryrhombus.proctimer.ui.timer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.os.Handler
import android.widget.Toast
import java.util.concurrent.TimeUnit

/**
 * タイマー用の ViewModel.
 */
class TimerViewModel(val app : Application) : AndroidViewModel(app) {

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

            if (timer.isEnded()) {
                /// TODO : ダイアログを表示するなどしたい. Fragment に委ねる?
                Toast.makeText(app.applicationContext, "Timer Ended", Toast.LENGTH_SHORT).show()
            }
            else {
                tickHandler.postDelayed(this, tickInterval)
            }
        }
    }

    private var isTicking = false

    init {
        updateText()
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

    private fun updateText() {
        val timerMilliseconds = timer.seconds.times(1000.0f).toLong()
        val minutes = TimeUnit.MILLISECONDS.toMinutes(timerMilliseconds)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(timerMilliseconds - TimeUnit.MINUTES.toMillis(minutes))
        val milliseconds = timerMilliseconds - TimeUnit.MINUTES.toMillis(minutes) - TimeUnit.SECONDS.toMillis(seconds)
        timerText.postValue("%02d:%02d.%03d".format(minutes, seconds, milliseconds))
    }
}
